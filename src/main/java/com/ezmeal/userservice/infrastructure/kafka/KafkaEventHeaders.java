package com.ezmeal.userservice.infrastructure.kafka;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class KafkaEventHeaders {

    public static final String USER_ID = "X-User-Id";
    public static final String ROLE = "X-User-Role";
}
