package com.ezmeal.userservice.infrastructure.kafka.config;

import com.ezmeal.userservice.infrastructure.kafka.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userCreatedTopic() {
        return new NewTopic(KafkaTopics.USER_CREATED, 1, (short) 1);
    }
}
