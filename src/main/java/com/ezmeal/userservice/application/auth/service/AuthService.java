package com.ezmeal.userservice.application.auth.service;

import com.ezmeal.userservice.infrastructure.client.KeycloakAuthClient;
import com.ezmeal.userservice.infrastructure.client.dto.KeycloakTokenResponse;
import com.ezmeal.userservice.presentation.user.payload.SignInRequest;
import com.ezmeal.userservice.presentation.user.payload.SignInResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KeycloakAuthClient keycloakAuthClient;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public SignInResponse signIn(SignInRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("username", request.email());
        form.add("password", request.password());
        form.add("client_secret", clientSecret);

        KeycloakTokenResponse tokenResponse = keycloakAuthClient.issueToken(form);

        return tokenResponse.toResponse();
    }
}
