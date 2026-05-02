package com.ezmeal.userservice.infrastructure.kafka.payload;

import com.ezmeal.userservice.application.user.event.UserCreateApplicationEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserCreatedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID userId,
    String keycloakId,
    String email,
    String name,
    String nickname,
    String role,
    LocalDateTime createdAt
) {

    public static UserCreatedEvent of(UserCreateApplicationEvent event) {
        return new UserCreatedEvent(
            UUID.randomUUID(),
            Instant.now(),
            event.userId(),
            event.keycloakId(),
            event.email(),
            event.name(),
            event.nickname(),
            event.role().toString(),
            event.createdAt()
        );
    }
}
