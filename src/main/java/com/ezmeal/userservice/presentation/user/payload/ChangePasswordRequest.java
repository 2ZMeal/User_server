package com.ezmeal.userservice.presentation.user.payload;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(

    @NotBlank(message="current password must be not blank")
    String currentPassword,

    @NotBlank(message="new password must be not blank")
    String newPassword
) {

}
