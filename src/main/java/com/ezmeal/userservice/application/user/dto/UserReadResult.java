package com.ezmeal.userservice.application.user.dto;

import com.ezmeal.common.enums.Role;
import com.ezmeal.userservice.domain.user.code.Status;
import com.ezmeal.userservice.domain.user.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserReadResult(
    UUID id,
    String keycloakId,
    String email,
    String name,
    String nickname,
    Role role,
    LocalDateTime lastLoginAt,
    Status status,
    LocalDateTime createdAt
) {

    public static UserReadResult from(User user) {
        return new UserReadResult(
            user.getId(),
            user.getKeycloakId(),
            user.getEmail(),
            user.getName(),
            user.getNickname(),
            user.getRole(),
            user.getLastLoginAt(),
            user.getStatus(),
            user.getCreatedAt()
        );
    }
}
