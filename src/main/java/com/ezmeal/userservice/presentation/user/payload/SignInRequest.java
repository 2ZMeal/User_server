package com.ezmeal.userservice.presentation.user.payload;

public record SignInRequest(
    String email,
    String password
) {

    @Override
    public String toString() {
        return "SignInRequest[email=%s, password=****]".formatted(email);
    }
}
