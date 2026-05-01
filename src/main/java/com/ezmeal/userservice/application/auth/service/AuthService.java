package com.ezmeal.userservice.application.auth.service;

import com.ezmeal.common.exception.types.InternalServerError;
import com.ezmeal.userservice.application.user.service.UserReadService;
import com.ezmeal.userservice.domain.exception.code.ResponseCode;
import com.ezmeal.userservice.infrastructure.client.KeycloakAdminClient;
import com.ezmeal.userservice.infrastructure.client.KeycloakAuthClient;
import com.ezmeal.userservice.infrastructure.client.dto.KeycloakTokenResponse;
import com.ezmeal.userservice.infrastructure.client.dto.ResetPasswordRequest;
import com.ezmeal.userservice.presentation.user.payload.ChangePasswordRequest;
import com.ezmeal.userservice.presentation.user.payload.LogoutRequest;
import com.ezmeal.userservice.presentation.user.payload.ReissueRequest;
import com.ezmeal.userservice.presentation.user.payload.SignInRequest;
import com.ezmeal.userservice.presentation.user.payload.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakAdminClient keycloakAdminClient;
    private final UserReadService userReadService;

    @Value("${keycloak.client.client-id}")
    private String clientId;

    @Value("${keycloak.client.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin.client-id}")
    private String adminClientId;

    @Value("${keycloak.admin.client-secret}")
    private String adminClientSecret;

    /**
     * Authenticates a user via Keycloak and returns an access token.
     * @param request ID/PW
     * @return {@link TokenResponse}
     */
    public TokenResponse signIn(SignInRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("username", request.email());
        form.add("password", request.password());
        form.add("client_secret", clientSecret);

        KeycloakTokenResponse tokenResponse = keycloakAuthClient.getToken(form);

        return tokenResponse.toResponse();
    }

    /**
     * Refreshes the access token via Keycloak using a refresh token.
     * @param request refresh token
     * @return {@link TokenResponse}
     */
    public TokenResponse reissue(ReissueRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("client_id", clientId);
        form.add("refresh_token", request.refreshToken());
        form.add("client_secret", clientSecret);

        KeycloakTokenResponse tokenResponse = keycloakAuthClient.getToken(form);

        return tokenResponse.toResponse();
    }

    /**
     * Logs out the user via Keycloak and invalidates the session.
     * @param request refresh token
     */
    public void logout(LogoutRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", request.refreshToken());

        keycloakAuthClient.logout(form);
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
        validateCurrentPassword(email, request.currentPassword());

        // 2. get admin access token
        String adminAccessToken = issueAdminAccessToken();

        // 3. get user's keycloak id via user id
        String keycloakId = userReadService.getKeycloakId(userId);

        // 4. send request as Keycloak admin
        keycloakAdminClient.resetPassword(
            "Bearer " + adminAccessToken,
            keycloakId,
            ResetPasswordRequest.of(request.newPassword())
        );
    }

    /**
     * validate User's current Password
     * @param email User's email
     * @param currentPassword entered password by user
     */
    private void validateCurrentPassword(String email, String currentPassword) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", email);
        form.add("password", currentPassword);

        try {
            keycloakAuthClient.getToken(form);
        } catch (Exception e) {
            log.error("AuthService :: validateCurrentPassword() - failed to validate password via keycloak");
            throw new InternalServerError(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get admin token via keycloak
     * @return {@link KeycloakTokenResponse}
     */
    private String issueAdminAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("grant_type", "client_credentials");
        form.add("client_id", adminClientId);
        form.add("client_secret", adminClientSecret);

        KeycloakTokenResponse tokenResponse = keycloakAuthClient.getToken(form);

        if(tokenResponse.accessToken() == null || tokenResponse.accessToken().isBlank()) {
            log.error("AuthService :: issueAdminAccessToken() - failed to get admin token via keycloak");
            throw new InternalServerError(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        return tokenResponse.accessToken();
    }
}
