package com.ezmeal.userservice.infrastructure.client.dto;

import com.ezmeal.userservice.presentation.user.payload.TokenResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KeycloakTokenResponse(

    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken,

    @JsonProperty("expires_in")
    long expiresIn,

    @JsonProperty("refresh_expires_in")
    long refreshExpiresIn,

    @JsonProperty("token_type")
    String tokenType
) {
    public TokenResponse toResponse() {
        return new TokenResponse(accessToken, refreshToken, expiresIn, refreshExpiresIn, tokenType);
    }
}
