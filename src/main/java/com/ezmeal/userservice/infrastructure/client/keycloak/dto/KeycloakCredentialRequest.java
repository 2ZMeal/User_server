package com.ezmeal.userservice.infrastructure.client.keycloak.dto;

public record KeycloakCredentialRequest(
    String type,
    String value,
    Boolean temporary
) {

    public static KeycloakCredentialRequest of(String newPassword) {
        return new KeycloakCredentialRequest(
            "password",
            newPassword,
            false
        );
    }
}
