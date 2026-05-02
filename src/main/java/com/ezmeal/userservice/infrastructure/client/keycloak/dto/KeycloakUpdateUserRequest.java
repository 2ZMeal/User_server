package com.ezmeal.userservice.infrastructure.client.keycloak.dto;

import java.util.List;
import java.util.Map;

public record KeycloakUpdateUserRequest(
    String username,
    String email,
    Boolean enabled,
    Boolean emailVerified,
    Map<String, List<String>> attributes
) {

    public static KeycloakUpdateUserRequest of(
        KeycloakUserResponse user,
        Map<String, List<String>> attributes
    ) {
        return new KeycloakUpdateUserRequest(
            user.username(),
            user.email(),
            user.enabled(),
            user.emailVerified(),
            attributes
        );
    }
}
