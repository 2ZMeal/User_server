package com.ezmeal.userservice.presentation.user.payload;

import com.ezmeal.userservice.application.user.dto.UpdateUserCommand;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
    @NotBlank
    String nickname,

    @NotBlank
    String name
) {

    public UpdateUserCommand toCommand() {
        return new UpdateUserCommand(
            nickname,
            name
        );
    }
}
