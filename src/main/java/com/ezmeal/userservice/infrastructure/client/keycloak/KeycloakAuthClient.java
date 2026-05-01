package com.ezmeal.userservice.infrastructure.client.keycloak;

import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakTokenResponse;
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
    KeycloakTokenResponse getToken(@RequestBody MultiValueMap<String, String> form);

    @PostMapping(
        value = "/realms/${keycloak.realm}/protocol/openid-connect/logout",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    void logout(@RequestBody MultiValueMap<String, String> form);
}
