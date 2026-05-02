package com.ezmeal.userservice.application.user.event;

import com.ezmeal.common.enums.Role;
import com.ezmeal.userservice.domain.user.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserCreateApplicationEvent(
    UUID userId,
    String keycloakId,
    String email,
    String name,
    String nickname,
    Role role,
    LocalDateTime createdAt
) {

    public static UserCreateApplicationEvent from(User user) {
        return new UserCreateApplicationEvent(
            user.getId(),
            user.getKeycloakId(),
            user.getEmail(),
            user.getName(),
            user.getNickname(),
            user.getRole(),
            user.getCreatedAt()
        );
    }
}
