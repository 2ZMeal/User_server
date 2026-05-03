package com.ezmeal.userservice.infrastructure.kafka;

import com.ezmeal.userservice.application.user.event.UserCreateApplicationEvent;
import com.ezmeal.userservice.application.user.event.UserDeletedApplicationEvent;
import com.ezmeal.userservice.infrastructure.kafka.payload.UserCreatedEvent;
import com.ezmeal.userservice.infrastructure.kafka.payload.UserDeletedEvent;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createEventPublish(UserCreateApplicationEvent event) {
        UserCreatedEvent payload = UserCreatedEvent.of(event);

        publishUserEvent(
            KafkaTopics.USER_CREATED,
            event.userId().toString(),
            payload,
            event.userId().toString(),
            event.role().name(),
            "user.created"
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteEventPublish(UserDeletedApplicationEvent event) {
        UserDeletedEvent payload = UserDeletedEvent.of(event);

        publishUserEvent(
            KafkaTopics.USER_DELETED,
            event.userId().toString(),
            payload,
            event.userId().toString(),
            event.role().name(),
            "user.deleted"
        );
    }

    private void publishUserEvent(
        String topic,
        String key,
        Object payload,
        String userId,
        String role,
        String eventName
    ) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(
            topic,
            key,
            payload
        );

        addHeader(record, KafkaEventHeaders.USER_ID, userId);
        addHeader(record, KafkaEventHeaders.ROLE, role);

        kafkaTemplate.send(record)
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error(
                        "KafkaProducer :: Failed to publish {} event. userId={}, role={}",
                        eventName,
                        userId,
                        role,
                        exception
                    );
                    return;
                }

                log.info(
                    "KafkaProducer :: Published {} event. topic={}, partition={}, offset={}",
                    eventName,
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            });
    }

    private void addHeader(
        ProducerRecord<String, Object> record,
        String key,
        String value
    ) {
        record.headers().add(
            key,
            value.getBytes(StandardCharsets.UTF_8)
        );
    }
}
