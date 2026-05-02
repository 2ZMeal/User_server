package com.ezmeal.userservice.presentation.user.payload;

import com.ezmeal.common.enums.Role;
import com.ezmeal.userservice.application.user.dto.SignUpCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(

    @Email
    @NotBlank
    @Size(min = 1, max = 30)
    String email,

    @NotBlank
    @Size(min=8, max = 30)
    String password,

    @Size(max=20)
    String nickname,

    @NotBlank
    @Size(max=10)
    String name,

    @NotBlank
    Role role
) {

    public SignUpCommand toCommand() {
        return new SignUpCommand(
            email,
            password,
            name,
            nickname,
            role
        );
    }
}
