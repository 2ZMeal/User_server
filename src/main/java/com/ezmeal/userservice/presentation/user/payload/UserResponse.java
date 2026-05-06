package com.ezmeal.userservice.presentation.user.payload;

import com.ezmeal.userservice.application.user.dto.UserReadResult;
import com.ezmeal.userservice.application.user.dto.UpdateUserResult;

public record UserResponse(
    String email,
    String nickname,
    String name
) {

    public static UserResponse of(UserReadResult result) {
        return new UserResponse(
            result.email(),
            result.nickname(),
            result.name()
        );
    }

    public static UserResponse of(UpdateUserResult result) {
        return new UserResponse(
            result.email(),
            result.nickname(),
            result.name()
        );
    }
}
