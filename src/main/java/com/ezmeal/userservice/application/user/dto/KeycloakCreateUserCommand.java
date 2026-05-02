package com.ezmeal.userservice.application.user.dto;

public record KeycloakCreateUserCommand(
    String email,
    String password
) {

}
