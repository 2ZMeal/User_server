package com.ezmeal.userservice.application.user.dto;

import com.ezmeal.userservice.domain.user.model.User;

public record UpdateUserResult(
    String email,
    String nickname,
    String name
) {

    public static UpdateUserResult from(User user) {
        return new UpdateUserResult(
            user.getEmail(),
            user.getNickname(),
            user.getName()
        );
    }
}
