package com.ezmeal.userservice.infrastructure.client.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KeycloakUserResponse(
    String id,
    String username,
    String email,
    Boolean enabled,
    Boolean emailVerified,
    Map<String, List<String>> attributes
) {

}
