package com.ezmeal.userservice.presentation.user.payload;

import com.ezmeal.common.enums.Role;
import com.ezmeal.userservice.application.user.dto.UserReadResult;
import com.ezmeal.userservice.domain.user.code.Status;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDetailsResponse(
    UUID userId,
    String keycloakId,
    String email,
    String name,
    String nickname,
    Role role,
    LocalDateTime lastLoginAt,
    Status status,
    LocalDateTime createdAt
) {

    public static UserDetailsResponse of(UserReadResult result) {
        return new UserDetailsResponse(
            result.id(),
            result.keycloakId(),
            result.email(),
            result.name(),
            result.nickname(),
            result.role(),
            result.lastLoginAt(),
            result.status(),
            result.createdAt()
        );
    }
}
