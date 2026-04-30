package com.ezmeal.userservice.infrastructure.client.dto;

public record ResetPasswordRequest(
    String type,
    String value,
    Boolean temporary
) {

    public static ResetPasswordRequest of(String newPassword) {
        return new ResetPasswordRequest(
            "password",
            newPassword,
            false
        );
    }
}
