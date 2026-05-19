package com.ezmeal.userservice.application.user.dto;

import com.ezmeal.common.enums.Role;
import lombok.Builder;

@Builder
public record SignUpCommand(
    String email,
    String password,
    String nickname,
    String name,
    Role role
) {
    public KeycloakCreateUserCommand toKeycloakCreateUserCommand() {
        return new KeycloakCreateUserCommand(
            email,
            password
        );
    }

    public CreateUserCommand toCreateUserCommand(String keycloakId) {
        return new CreateUserCommand(
            keycloakId,
            email,
            nickname,
            name,
            role
        );
    }
}
