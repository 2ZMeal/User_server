package com.ezmeal.userservice.application.user.dto;

public record UpdateUserCommand(
    String nickname,
    String name
) {

}
