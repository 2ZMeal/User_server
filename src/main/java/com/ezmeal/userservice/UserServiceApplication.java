package com.ezmeal.userservice;

import com.ezmeal.userservice.infrastructure.client.keycloak.config.KeycloakProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableFeignClients
@EnableDiscoveryClient
@EnableConfigurationProperties(KeycloakProperties.class)
@EnableJpaRepositories(basePackages = {
    "com.ezmeal.userservice",
    "com.ezmeal.common.message.outbox"
})
@SpringBootApplication(scanBasePackages = {
    "com.ezmeal.userservice",
    "com.ezmeal.common"
})
@EntityScan(basePackages = {
    "com.ezmeal.userservice",
    "com.ezmeal.common"
})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
