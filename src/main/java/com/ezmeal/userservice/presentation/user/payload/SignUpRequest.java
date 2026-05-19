package com.ezmeal.userservice.presentation.user.payload;

import com.ezmeal.common.enums.Role;
import com.ezmeal.userservice.application.user.dto.SignUpCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest(

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    @Size(min = 1, max = 30)
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.(8~30자)")
    @Size(min=8, max = 30)
    String password,

    @Size(max=20, message = "닉네임은 20자 이하로 작성해주세요.")
    String nickname,

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max=10)
    String name,

    @NotNull(message = "Role must be not null")
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
