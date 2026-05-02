package com.ezmeal.userservice.infrastructure.client.keycloak;

import com.ezmeal.common.enums.Role;
import com.ezmeal.common.exception.CustomException;
import com.ezmeal.userservice.application.user.dto.KeycloakCreateUserCommand;
import com.ezmeal.userservice.common.exception.code.ResponseCode;
import com.ezmeal.userservice.infrastructure.client.keycloak.config.KeycloakProperties;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakCreateUserRequest;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakCredentialRequest;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakRoleResponse;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakTokenResponse;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakUpdateUserRequest;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakUserResponse;
import com.ezmeal.userservice.infrastructure.client.keycloak.exception.KeycloakExceptionMapper;
import feign.FeignException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAdminAdapter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String SERVICE_USER_ID_ATTRIBUTE = "service_user_id";

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakAdminClient keycloakAdminClient;
    private final KeycloakProperties keycloakProperties;

    public String createUser(KeycloakCreateUserCommand command) {
        String adminAccessToken = issueAdminAccessToken();

        try {
            ResponseEntity<Void> response = keycloakAdminClient.createUser(
                bearer(adminAccessToken),
                KeycloakCreateUserRequest.from(command)
            );

            URI location = response.getHeaders().getLocation();
            return extractKeycloakUserId(location);
        } catch (FeignException e) {
            log.error(
                "KeycloakAdminAdapter :: createUser() - failed. status={}",
                e.status(),
                e
            );
            throw KeycloakExceptionMapper.map(e, KeycloakOperation.CREATE_USER);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("KeycloakAdminAdapter :: createUser() - unexpected error", e);
            throw new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }
    }

    public void assignRealmRole(String keycloakUserId, Role role) {
        String adminAccessToken = issueAdminAccessToken();

        try {
            KeycloakRoleResponse roleResponse = keycloakAdminClient.getRealmRole(
                bearer(adminAccessToken),
                role.name()
            );

            keycloakAdminClient.addRealmRole(
                bearer(adminAccessToken),
                keycloakUserId,
                List.of(roleResponse)
            );

        } catch (FeignException e) {
            log.error(
                "KeycloakAdminAdapter :: assignRealmRole() - failed. status={}, keycloakUserId={}, role={}",
                e.status(),
                keycloakUserId,
                role,
                e
            );
            throw KeycloakExceptionMapper.map(e, KeycloakOperation.ASSIGN_REALM_ROLE);
        } catch (Exception e) {
            log.error("KeycloakAdminAdapter :: assignRealmRole() - unexpected error", e);
            throw new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }
    }

    public void updateServiceUserIdAttribute(String keycloakUserId, String serviceUserId) {
        String adminAccessToken = issueAdminAccessToken();

        try {
            KeycloakUserResponse user = keycloakAdminClient.getUser(
                bearer(adminAccessToken),
                keycloakUserId
            );

            Map<String, List<String>> attributes = user.attributes() == null
                ? new HashMap<>()
                : new HashMap<>(user.attributes());

            attributes.put(SERVICE_USER_ID_ATTRIBUTE, List.of(serviceUserId));

            KeycloakUpdateUserRequest request = KeycloakUpdateUserRequest.of(user, attributes);

            keycloakAdminClient.updateUser(
                bearer(adminAccessToken),
                keycloakUserId,
                request
            );

        } catch (FeignException e) {
            log.error(
                "KeycloakAdminAdapter :: updateServiceUserIdAttribute() - failed. status={}, keycloakUserId={}, serviceUserId={}",
                e.status(),
                keycloakUserId,
                serviceUserId,
                e
            );
            throw KeycloakExceptionMapper.map(e, KeycloakOperation.UPDATE_USER_ATTRIBUTE);
        } catch (Exception e) {
            log.error("KeycloakAdminAdapter :: updateServiceUserIdAttribute() - unexpected error",
                e);
            throw new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }
    }

    public void deleteUser(String keycloakUserId) {
        String adminAccessToken = issueAdminAccessToken();

        try {
            keycloakAdminClient.deleteUser(
                bearer(adminAccessToken),
                keycloakUserId
            );

        } catch (FeignException e) {
            log.error(
                "KeycloakAdminAdapter :: deleteUser() - failed. status={}, keycloakUserId={}",
                e.status(),
                keycloakUserId,
                e
            );
            throw KeycloakExceptionMapper.map(e, KeycloakOperation.DELETE_USER);
        } catch (Exception e) {
            log.error("KeycloakAdminAdapter :: deleteUser() - unexpected error", e);
            throw new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }
    }

    public void changePassword(String keycloakUserId, String newPassword) {
        String adminAccessToken = issueAdminAccessToken();

        try {
            keycloakAdminClient.resetPassword(
                bearer(adminAccessToken),
                keycloakUserId,
                KeycloakCredentialRequest.of(newPassword)
            );
        } catch (FeignException e) {
            log.error(
                "KeycloakAdminAdapter :: changePassword() - failed. status={}, keycloakUserId={}",
                e.status(),
                keycloakUserId,
                e
            );
            throw KeycloakExceptionMapper.map(e, KeycloakOperation.CHANGE_PASSWORD);
        } catch (Exception e) {
            log.error("KeycloakAdminAdapter :: changePassword() - unexpected error", e);
            throw new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }
    }

    // HELPER METHODS ==================================================

    /**
     * Get admin token via keycloak
     *
     * @return {@code String} - access token
     */
    private String issueAdminAccessToken() {
        try {
            KeycloakTokenResponse tokenResponse =
                keycloakAuthClient.getToken(createAdminClientCredentialsForm());

            if (tokenResponse.accessToken() == null || tokenResponse.accessToken().isBlank()) {
                log.error("KeycloakAdminAdapter :: issueAdminAccessToken() - empty access token");
                throw new CustomException(ResponseCode.KEYCLOAK_TOKEN_ISSUE_FAILED);
            }

            return tokenResponse.accessToken();

        } catch (FeignException e) {
            log.error(
                "KeycloakAdminAdapter :: issueAdminAccessToken() - failed. status={}",
                e.status(),
                e
            );
            throw KeycloakExceptionMapper.map(e, KeycloakOperation.ISSUE_ADMIN_ACCESS_TOKEN);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("KeycloakAdminAdapter :: issueAdminAccessToken() - unexpected error", e);
            throw new CustomException(ResponseCode.KEYCLOAK_TOKEN_ISSUE_FAILED);
        }
    }

    private String bearer(String token) {
        return BEARER_PREFIX + token;
    }

    private String extractKeycloakUserId(URI location) {
        if (location == null) {
            log.error("KeycloakAdminAdapter :: extractKeycloakUserId() - Location header is null");
            throw new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }

        String path = location.getPath();
        if (path == null || path.isBlank()) {
            log.error("KeycloakAdminAdapter :: extractKeycloakUserId() - Location path is empty");
            throw new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }

        String userId = path.substring(path.lastIndexOf('/') + 1);
        if (userId.isBlank()) {
            log.error(
                "KeycloakAdminAdapter :: extractKeycloakUserId() - Extracted userId is blank. location={}",
                location);
            throw new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }

        return userId;
    }

    // CREATE FORM =====================================================
    private MultiValueMap<String, String> createAdminClientCredentialsForm() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("grant_type", "client_credentials");
        form.add("client_id", keycloakProperties.adminClientId());
        form.add("client_secret", keycloakProperties.adminClientSecret());

        return form;
    }
}
