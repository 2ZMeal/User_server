package com.ezmeal.userservice.infrastructure.client;

import com.ezmeal.userservice.infrastructure.client.dto.KeycloakTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "keycloak-auth-client",
    url = "${keycloak.auth-server-url}"
)
public interface KeycloakAuthClient {

    @PostMapping(
        value = "/realms/${keycloak.realm}/protocol/openid-connect/token",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    KeycloakTokenResponse issueToken(@RequestBody MultiValueMap<String, String> form);
}
