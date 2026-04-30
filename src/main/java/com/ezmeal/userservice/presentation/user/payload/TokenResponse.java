package com.ezmeal.userservice.presentation.user.payload;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    long refreshExpiresIn,
    String tokenType
) {

}
