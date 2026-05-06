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
    FORBIDDEN("USER_001", HttpStatus.FORBIDDEN, "Access denied"),

    // KEYCLOAK(1)
    KEYCLOAK_INVALID_USER_CREDENTIALS("USER_100", HttpStatus.BAD_REQUEST, "Invalid user credentials"),
    KEYCLOAK_UNAVAILABLE("USER_101", HttpStatus.SERVICE_UNAVAILABLE, "Unable to connect to the authentication server."),
    KEYCLOAK_REQUEST_FAILED("USER_102", HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing the request on the authentication server."),
    KEYCLOAK_TOKEN_ISSUE_FAILED("USER_103", HttpStatus.INTERNAL_SERVER_ERROR, "Token issue failed"),
    KEYCLOAK_INVALID_GRANT("USER_104", HttpStatus.BAD_REQUEST, "Invalid grant" ),
    KEYCLOAK_ROLE_NOT_FOUND("USER_105", HttpStatus.NOT_FOUND, "Role not found"),
    KEYCLOAK_USER_NOT_FOUND("USER_106", HttpStatus.NOT_FOUND, "User not found"),

    // USER(2),
    USER_NOT_FOUND("USER_200", HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS("USER_201", HttpStatus.CONFLICT, "User already exists"),
    NICKNAME_ALREADY_EXISTS("USER_202", HttpStatus.CONFLICT, "Nickname already exists"),


    ;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
