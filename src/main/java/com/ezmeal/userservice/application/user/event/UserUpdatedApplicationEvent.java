package com.ezmeal.userservice.application.user.event;

import com.ezmeal.userservice.domain.user.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserUpdatedApplicationEvent (
    UUID userId,
    String name,
    String nickname,
    String email,
    String role,
    String status,
    LocalDateTime signupAt,
    LocalDateTime lastLoginAt
) {

    public static UserUpdatedApplicationEvent from(User user) {
        return new UserUpdatedApplicationEvent(
            user.getId(),
            user.getName(),
            user.getNickname(),
            user.getEmail(),
            user.getRole().toString(),
            user.getStatus().toString(),
            user.getCreatedAt(),
            user.getLastLoginAt()
        );
    }
}
