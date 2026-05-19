package com.ezmeal.userservice.infrastructure.kafka.payload;

import com.ezmeal.common.enums.Role;
import com.ezmeal.common.message.DomainEvent;
import com.ezmeal.userservice.application.user.event.UserUpdatedApplicationEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserUpdatedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID userId,
    String name,
    String nickname,
    String email,
    String role,
    String status,
    LocalDateTime signupAt,
    LocalDateTime lastLoginAt
) implements DomainEvent {

    public static UserUpdatedEvent of(UserUpdatedApplicationEvent event) {
        return new UserUpdatedEvent(
            UUID.randomUUID(),
            Instant.now(),
            event.userId(),
            event.name(),
            event.nickname(),
            event.email(),
            event.role(),
            event.status(),
            event.signupAt(),
            event.lastLoginAt()
        );
    }
}
