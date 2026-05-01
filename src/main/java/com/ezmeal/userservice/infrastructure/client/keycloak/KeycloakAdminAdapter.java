package com.ezmeal.userservice.infrastructure.client.keycloak;

import com.ezmeal.common.exception.CustomException;
import com.ezmeal.userservice.common.exception.code.ResponseCode;
import com.ezmeal.userservice.infrastructure.client.keycloak.config.KeycloakProperties;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakPasswordUpdateRequest;
import com.ezmeal.userservice.infrastructure.client.keycloak.dto.KeycloakTokenResponse;
import com.ezmeal.userservice.infrastructure.client.keycloak.exception.KeycloakExceptionMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAdminAdapter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakAdminClient keycloakAdminClient;
    private final KeycloakProperties keycloakProperties;

    public void changePassword(String keycloakUserId, String newPassword) {
        String adminAccessToken = issueAdminAccessToken();

        try {
            keycloakAdminClient.resetPassword(
                BEARER_PREFIX + adminAccessToken,
                keycloakUserId,
                KeycloakPasswordUpdateRequest.of(newPassword)
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

    /**
     * Get admin token via keycloak
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

    private MultiValueMap<String, String> createAdminClientCredentialsForm() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("grant_type", "client_credentials");
        form.add("client_id", keycloakProperties.adminClientId());
        form.add("client_secret", keycloakProperties.adminClientSecret());

        return form;
    }
}
