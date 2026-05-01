package com.ezmeal.userservice.application.auth.service;

import com.ezmeal.userservice.application.user.service.UserReadService;
import com.ezmeal.userservice.infrastructure.client.keycloak.KeycloakAdminAdapter;
import com.ezmeal.userservice.infrastructure.client.keycloak.KeycloakAuthAdapter;
import com.ezmeal.userservice.presentation.user.payload.ChangePasswordRequest;
import com.ezmeal.userservice.presentation.user.payload.LogoutRequest;
import com.ezmeal.userservice.presentation.user.payload.ReissueRequest;
import com.ezmeal.userservice.presentation.user.payload.SignInRequest;
import com.ezmeal.userservice.presentation.user.payload.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakAuthAdapter keycloakAuthAdapter;
    private final KeycloakAdminAdapter keycloakAdminAdapter;
    private final UserReadService userReadService;

    /**
     * Authenticates a user via Keycloak and returns an access token.
     * @param request ID/PW
     * @return {@link TokenResponse}
     */
    public TokenResponse signIn(SignInRequest request) {
        return keycloakAuthAdapter.getTokenResponse(request.email(), request.password()).toResponse();
    }

    /**
     * Refreshes the access token via Keycloak using a refresh token.
     * @param request refresh token
     * @return {@link TokenResponse}
     */
    public TokenResponse reissue(ReissueRequest request) {
        return keycloakAuthAdapter.reissueToken(request.refreshToken()).toResponse();
    }

    /**
     * Logs out the user via Keycloak and invalidates the session.
     * @param request refresh token
     */
    public void logout(LogoutRequest request) {
        keycloakAuthAdapter.logout(request.refreshToken());
    }

    /**
     * Change User's password via Keycloak
     * @param userId id from p_user
     * @param email user's email(login_id)
     * @param request {@link ChangePasswordRequest}
     */
    @Transactional
    public void changePassword(String userId, String email, ChangePasswordRequest request) {
        // 1. validate current password
        keycloakAuthAdapter.getTokenResponse(email, request.currentPassword());

        // 2. get user's keycloak id via user id
        String keycloakId = userReadService.getKeycloakId(userId);

        // 3. send password update request as Keycloak admin
        keycloakAdminAdapter.changePassword(keycloakId, request.newPassword());
    }
}
