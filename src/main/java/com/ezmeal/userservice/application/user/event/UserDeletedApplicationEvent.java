package com.ezmeal.userservice.application.user.event;

import com.ezmeal.common.enums.Role;
import com.ezmeal.userservice.domain.user.model.User;
import java.util.UUID;

public record UserDeletedApplicationEvent(
    UUID userId,
    Role role
) {

    public static UserDeletedApplicationEvent from(User user) {
        return new UserDeletedApplicationEvent(
            user.getId(),
            user.getRole()
        );
    }
}
