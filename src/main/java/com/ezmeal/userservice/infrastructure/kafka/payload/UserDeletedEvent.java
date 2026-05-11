package com.ezmeal.userservice.infrastructure.kafka.payload;

import com.ezmeal.common.enums.Role;
import com.ezmeal.common.message.DomainEvent;
import com.ezmeal.userservice.application.user.event.UserDeletedApplicationEvent;
import java.time.Instant;
import java.util.UUID;

public record UserDeletedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID userId,
    Role role
) implements DomainEvent {

    public static UserDeletedEvent of(UserDeletedApplicationEvent event) {
        return new UserDeletedEvent(
            UUID.randomUUID(),
            Instant.now(),
            event.userId(),
            event.role()
        );
    }
}
