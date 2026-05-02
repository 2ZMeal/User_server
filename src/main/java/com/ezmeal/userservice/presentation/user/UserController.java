package com.ezmeal.userservice.presentation.user;

import com.ezmeal.common.response.CommonApiResponse;
import com.ezmeal.common.security.principal.CustomUserPrincipal;
import com.ezmeal.userservice.application.user.service.UserService;
import com.ezmeal.userservice.presentation.user.payload.ChangePasswordRequest;
import com.ezmeal.userservice.presentation.user.payload.LogoutRequest;
import com.ezmeal.userservice.presentation.user.payload.ReissueRequest;
import com.ezmeal.userservice.presentation.user.payload.SignInRequest;
import com.ezmeal.userservice.presentation.user.payload.SignUpRequest;
import com.ezmeal.userservice.presentation.user.payload.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
        @RequestBody SignUpRequest request
    ) {
        return ResponseEntity.ok(CommonApiResponse.success(userService.signUp(request.toCommand())));
    }
}
