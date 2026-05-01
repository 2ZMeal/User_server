package com.ezmeal.userservice.common.exception.code;

import com.ezmeal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode implements ErrorCode {
    // COMMON(0)
    INTERNAL_SERVER_ERROR("USER_000", HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    // AUTH(1)
    KEYCLOAK_INVALID_USER_CREDENTIALS("USER_100", HttpStatus.BAD_REQUEST, "Invalid user credentials"),
    KEYCLOAK_UNAVAILABLE("USER_101", HttpStatus.SERVICE_UNAVAILABLE, "Unable to connect to the authentication server."),
    KEYCLOAK_REQUEST_FAILED("USER_102", HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing the request on the authentication server."),
    KEYCLOAK_TOKEN_ISSUE_FAILED("USER_103", HttpStatus.INTERNAL_SERVER_ERROR, "Token issue failed"),
    KEYCLOAK_INVALID_GRANT("USER_104", HttpStatus.BAD_REQUEST, "Invalid grant" ),

    // USER(2),
    USER_NOT_FOUND("USER_200", HttpStatus.NOT_FOUND, "User not found");


    private final String code;
    private final HttpStatus status;
    private final String message;
}
