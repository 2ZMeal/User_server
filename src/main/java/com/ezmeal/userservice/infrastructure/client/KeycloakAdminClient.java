package com.ezmeal.userservice.infrastructure.client;

import com.ezmeal.userservice.infrastructure.client.dto.ResetPasswordRequest;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "keycloak-admin-client",
    url = "${keycloak.auth-server-url}"
)
public interface KeycloakAdminClient {

    @PutMapping(
        value = "/admin/realms/${keycloak.realm}/users/{userId}/reset-password",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    void resetPassword(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String userId,
        @RequestBody ResetPasswordRequest request
    );
}
