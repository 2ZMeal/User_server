package com.ezmeal.userservice.infrastructure.kafka;

import com.ezmeal.common.message.CommonKafkaEventPublisher;
import com.ezmeal.userservice.application.user.event.UserCreatedApplicationEvent;
import com.ezmeal.userservice.application.user.event.UserDeletedApplicationEvent;
import com.ezmeal.userservice.infrastructure.kafka.payload.UserCreatedEvent;
import com.ezmeal.userservice.infrastructure.kafka.payload.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final CommonKafkaEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishCreateEvent(UserCreatedApplicationEvent event) {
        eventPublisher.publish(
            KafkaTopics.USER_CREATED,
            event.userId().toString(),
            "USER_CREATED",
            UserCreatedEvent.of(event)
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishDeletedEvent(UserDeletedApplicationEvent event) {
        eventPublisher.publish(
            KafkaTopics.USER_DELETED,
            event.userId().toString(),
            "USER_DELETED",
            UserDeletedEvent.of(event)
        );
    }
}
