package com.ezmeal.userservice.infrastructure.client.keycloak;

import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakCreateUserRequest;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakCredentialRequest;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakRoleResponse;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakUpdateUserRequest;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakUserResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void resetPassword(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String userId,
        @RequestBody KeycloakCredentialRequest request
    );

    @PostMapping(
        value="/admin/realms/${keycloak.realm}/users",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> createUser(
        @RequestHeader("Authorization") String authorization,
        @RequestBody KeycloakCreateUserRequest request
    );

    @GetMapping(
        value="/admin/realms/${keycloak.realm}/roles/{roleName}"
    )
    KeycloakRoleResponse getRealmRole(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String roleName
    );

    @PostMapping(
        value="/admin/realms/${keycloak.realm}/users/{userId}/role-mappings/realm",
        consumes=MediaType.APPLICATION_JSON_VALUE
    )
    void addRealmRole(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String userId,
        @RequestBody List<KeycloakRoleResponse> roles
    );

    @GetMapping(
        value = "/admin/realms/${keycloak.realm}/users/{userId}"
    )
    KeycloakUserResponse getUser(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String userId
    );

    @PostMapping(
        value="/admin/realms/${keycloak.realm}/users/{userId}",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void updateUser(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String userId,
        @RequestBody KeycloakUpdateUserRequest request
    );

    @DeleteMapping(
        value = "/admin/realms/${keycloak.realm}/users/{userId}"
    )
    void deleteUser(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String userId
    );
}
