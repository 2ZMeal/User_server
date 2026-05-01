package com.ezmeal.userservice.infrastructure.client.keycloak.dto;

public record KeycloakPasswordUpdateRequest(
    String type,
    String value,
    Boolean temporary
) {

    public static KeycloakPasswordUpdateRequest of(String newPassword) {
        return new KeycloakPasswordUpdateRequest(
            "password",
            newPassword,
            false
        );
    }
}
