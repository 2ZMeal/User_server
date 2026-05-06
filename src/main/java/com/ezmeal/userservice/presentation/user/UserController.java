package com.ezmeal.userservice.presentation.user;

import com.ezmeal.common.enums.Role;
import com.ezmeal.common.exception.types.ForbiddenException;
import com.ezmeal.common.response.CommonApiResponse;
import com.ezmeal.common.security.principal.CustomUserPrincipal;
import com.ezmeal.userservice.application.user.service.UserReadService;
import com.ezmeal.userservice.application.user.service.UserService;
import com.ezmeal.userservice.common.exception.code.ResponseCode;
import com.ezmeal.userservice.presentation.user.payload.ChangePasswordRequest;
import com.ezmeal.userservice.presentation.user.payload.LogoutRequest;
import com.ezmeal.userservice.presentation.user.payload.ReissueRequest;
import com.ezmeal.userservice.presentation.user.payload.SignInRequest;
import com.ezmeal.userservice.presentation.user.payload.SignUpRequest;
import com.ezmeal.userservice.presentation.user.payload.TokenResponse;
import com.ezmeal.userservice.presentation.user.payload.UpdateUserRequest;
import com.ezmeal.userservice.presentation.user.payload.UserDetailsPageResponse;
import com.ezmeal.userservice.presentation.user.payload.UserDetailsResponse;
import com.ezmeal.userservice.presentation.user.payload.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserReadService userReadService;

    @PostMapping("/signin")
    public ResponseEntity<CommonApiResponse<TokenResponse>> signIn(
        @RequestBody SignInRequest request
    ) {
        return ResponseEntity.ok(CommonApiResponse.success(userService.signIn(request)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<CommonApiResponse<TokenResponse>> reissue(
        @RequestBody ReissueRequest request
    ) {
        return ResponseEntity.ok(CommonApiResponse.success(userService.reissue(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonApiResponse<Void>> logout(
        @RequestBody LogoutRequest request
    ) {
        userService.logout(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(CommonApiResponse.success());
    }

    @PatchMapping("/me/password")
    public ResponseEntity<CommonApiResponse<Void>> changePassword(
        @AuthenticationPrincipal CustomUserPrincipal principal,
        @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(principal.getUserId(), principal.getEmail(), request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(CommonApiResponse.success());
    }

    @PostMapping("/signup")
    public ResponseEntity<CommonApiResponse<TokenResponse>> signup(
        @RequestBody @Valid SignUpRequest request
    ) {
        return ResponseEntity.ok(CommonApiResponse.success(userService.signUp(request.toCommand())));
    }

    @GetMapping("/me")
    public ResponseEntity<CommonApiResponse<UserResponse>> getUser(
        @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(CommonApiResponse.success(UserResponse.of(
            userReadService.getUser(UUID.fromString(principal.getUserId()))
        )));
    }

    @Operation(summary = "[관리자] 회원 목록 조회", description = "회원 목록을 조회합니다.<br>ADMIN 권한만 가능합니다.")
    @GetMapping
    public ResponseEntity<CommonApiResponse<UserDetailsPageResponse>> getUserList(
        @AuthenticationPrincipal CustomUserPrincipal principal,
        @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
       validateRoleAdmin(principal.getRole());
       return ResponseEntity.ok(CommonApiResponse.success(
           UserDetailsPageResponse.of(userReadService.getUserList(pageable))
       ));
    }

    @Operation(summary = "[관리자] 회원 상세 조회", description = "회원을 상세 조회합니다.<br>ADMIN 권한만 가능합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<CommonApiResponse<UserDetailsResponse>> getUserDetails(
        @AuthenticationPrincipal CustomUserPrincipal principal,
        @PathVariable(value="userId") String id
    ) {
        validateRoleAdmin(principal.getRole());
        return ResponseEntity.ok(CommonApiResponse.success(
           UserDetailsResponse.of(userReadService.getUser(UUID.fromString(principal.getUserId())))
        ));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<CommonApiResponse<Void>> withdraw(
        @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        userService.withdraw(UUID.fromString(principal.getUserId()));
        return ResponseEntity.ok(CommonApiResponse.success());
    }

    @Operation(summary = "회원정보 수정", description = "회원의 본인 정보를 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<CommonApiResponse<?>> updateMe(
        @AuthenticationPrincipal CustomUserPrincipal principal,
        @RequestBody @Valid UpdateUserRequest request
    ) {
        return ResponseEntity.ok(CommonApiResponse.success(
           UserResponse.of(userService.updateUser(UUID.fromString(principal.getUserId()), request.toCommand()))
        ));
    }

    @Operation(summary = "닉네임 중복 검사", description = "닉네임 중복검사를 수행합니다.")
    @GetMapping("/check")
    public ResponseEntity<CommonApiResponse<Void>> checkNickname(
        @RequestParam String nickname
    ) {
        userReadService.validateNicknameExists(nickname);
        return ResponseEntity.ok(CommonApiResponse.success());
    }

    // HELPER METHODS =================================================
    private void validateRoleAdmin(Role role) {
        if(!role.equals(Role.ADMIN)) throw new ForbiddenException(ResponseCode.FORBIDDEN);
    }
}
