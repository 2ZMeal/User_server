package com.ezmeal.userservice.domain.exception.code;

import com.ezmeal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode implements ErrorCode {
    INTERNAL_SERVER_ERROR("USER_000", HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    USER_NOT_FOUND("USER_001", HttpStatus.NOT_FOUND, "User not found"),

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
