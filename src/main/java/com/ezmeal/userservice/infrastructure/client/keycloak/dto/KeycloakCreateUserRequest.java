package com.ezmeal.userservice.infrastructure.client.keycloak.dto;

import com.ezmeal.userservice.application.user.dto.KeycloakCreateUserCommand;
import java.util.List;

public record KeycloakCreateUserRequest(
    String username,
    String email,
    Boolean enabled,
    Boolean emailVerified,
    List<KeycloakCredentialRequest> credentials
) {

    public static KeycloakCreateUserRequest from(KeycloakCreateUserCommand command) {
        return new KeycloakCreateUserRequest(
            command.email(),
            command.email(),
            true,
            false,
            List.of(KeycloakCredentialRequest.of(command.password()))
        );
    }
}
