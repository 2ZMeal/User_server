package com.ezmeal.userservice.infrastructure.client.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KeycloakRoleResponse(
    String id,
    String name,
    String description,
    Boolean composite,
    Boolean clientRole,
    String containerId
) {

}
