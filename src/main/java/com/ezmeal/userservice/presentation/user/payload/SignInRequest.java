package com.ezmeal.userservice.presentation.user.payload;

public record SignInRequest(
    String email,
    String password
) {

}
