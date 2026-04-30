package com.ezmeal.userservice.presentation.user.payload;

public record SignInResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    long refreshExpiresIn,
    String tokenType
) {

}
