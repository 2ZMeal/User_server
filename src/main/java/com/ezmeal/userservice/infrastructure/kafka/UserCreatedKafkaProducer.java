package com.ezmeal.userservice.infrastructure.kafka;

import com.ezmeal.userservice.application.user.event.UserCreateApplicationEvent;
import com.ezmeal.userservice.infrastructure.kafka.payload.UserCreatedEvent;
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
public class UserCreatedKafkaProducer {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    @TransactionalEventListener(phase= TransactionPhase.AFTER_COMMIT)
    public void publish(UserCreateApplicationEvent event) {
        UserCreatedEvent payload = UserCreatedEvent.of(event);

        ProducerRecord<String, UserCreatedEvent> record = new ProducerRecord<>(
            KafkaTopics.USER_CREATED, event.userId().toString(), payload
        );

        record.headers().add(
            KafkaEventHeaders.USER_ID,
            event.userId().toString().getBytes(StandardCharsets.UTF_8)
        );

        record.headers().add(
            KafkaEventHeaders.ROLE,
            event.role().name().getBytes(StandardCharsets.UTF_8)
        );

        kafkaTemplate.send(record)
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error(
                        "UserCreatedKafkaProducer :: Failed to publish user.created event. userId={}, role={}",
                        event.userId(),
                        event.role(),
                        exception
                    );
                    return;
                }

                log.info(
                    "UserCreatedKafkaProducer :: Published user.created event. topic={}, partition={}, offset={}",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            });
    }
}
