package com.ezmeal.userservice.presentation.user.payload;

public record LogoutRequest(
    String refreshToken
) {

}
