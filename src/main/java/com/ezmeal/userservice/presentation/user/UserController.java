package com.ezmeal.userservice.presentation.user;

import com.ezmeal.common.response.CommonApiResponse;
import com.ezmeal.userservice.application.auth.service.AuthService;
import com.ezmeal.userservice.presentation.user.payload.LogoutRequest;
import com.ezmeal.userservice.presentation.user.payload.ReissueRequest;
import com.ezmeal.userservice.presentation.user.payload.SignInRequest;
import com.ezmeal.userservice.presentation.user.payload.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<CommonApiResponse<TokenResponse>> signIn(
        @RequestBody SignInRequest request
    ) {
        return ResponseEntity.ok(CommonApiResponse.success(authService.signIn(request)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<CommonApiResponse<TokenResponse>> reissue(
        @RequestBody ReissueRequest request
    ) {
        return ResponseEntity.ok(CommonApiResponse.success(authService.reissue(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonApiResponse<Void>> logout(
        @RequestBody LogoutRequest request
    ) {
        authService.logout(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(CommonApiResponse.success());
    }
}
