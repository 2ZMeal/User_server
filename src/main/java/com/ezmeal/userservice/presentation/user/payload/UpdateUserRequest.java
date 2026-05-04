package com.ezmeal.userservice.presentation.user.payload;

import com.ezmeal.userservice.application.user.dto.UpdateUserCommand;

public record UpdateUserRequest(
    String nickname,
    String name
) {

    public UpdateUserCommand toCommand() {
        return new UpdateUserCommand(
            nickname,
            name
        );
    }
}
