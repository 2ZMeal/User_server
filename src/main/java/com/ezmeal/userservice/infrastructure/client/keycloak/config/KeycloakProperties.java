package com.ezmeal.userservice.infrastructure.client.keycloak.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties (
    String realm,
    String clientId,
    String clientSecret,
    String adminClientId,
    String adminClientSecret
) {

}
