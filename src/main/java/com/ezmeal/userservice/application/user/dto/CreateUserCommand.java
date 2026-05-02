package com.ezmeal.userservice.application.user.dto;

import com.ezmeal.common.enums.Role;

public record CreateUserCommand(
    String keycloakId,
    String email,
    String name,
    String nickname,
    Role role
) {

}
