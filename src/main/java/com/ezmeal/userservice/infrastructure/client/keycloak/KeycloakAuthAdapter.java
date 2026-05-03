package com.ezmeal.userservice.infrastructure.client.keycloak;

import com.ezmeal.common.exception.CustomException;
import com.ezmeal.userservice.common.exception.code.ResponseCode;
import com.ezmeal.userservice.infrastructure.client.keycloak.config.KeycloakProperties;
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
public class KeycloakAuthAdapter {

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakProperties keycloakProperties;

    /**
     * get token via keycloak
     *
     * @param email
     * @param password
     */
    public KeycloakTokenResponse getTokenResponse(String email, String password) {
        try {
            return keycloakAuthClient.getToken(createTokenIssueForm(email, password));
        } catch (FeignException e) {
            log.warn(
                "KeycloakAuthAdapter :: getTokenResponse() - failed. status={}, email={}, body={}",
                e.status(),
                email,
                e.contentUTF8(),
                e
            );
            throw KeycloakExceptionMapper.map(e, KeycloakOperation.GET_TOKEN_RESPONSE);
        } catch (Exception e) {
            log.error("KeycloakAuthAdapter :: getTokenResponse() - unexpected error", e);
            throw new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }
    }

    /**
     * reissue token via keycloak
     *
     * @param refreshToken
     * @return {@lilnk KeycloakTokenResponse}
     */
    public KeycloakTokenResponse reissueToken(String refreshToken) {
        try {
            return keycloakAuthClient.getToken(createTokenReIssueForm(refreshToken));
        } catch (FeignException e) {
            log.warn(
                "KeycloakAuthAdapter :: reissueToken() - failed. status={}", e.status()
            );
            throw KeycloakExceptionMapper.map(e, KeycloakOperation.GET_TOKEN_RESPONSE);
        }
    }

    /**
     * Request logout to keycloak<br>
     * Terminate the session based on the refresh token.
     * @param refreshToken
     */
    public void logout(String refreshToken) {
        try {
            keycloakAuthClient.logout(createLogOutForm(refreshToken));
        } catch(FeignException.BadRequest e) {
          log.info("KeycloakAuthAdapter :: logoud() - CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);");
        } catch (FeignException.Unauthorized e) {
            log.warn(
                "KeycloakAuthAdapter :: logout() - failed. status={}", e.status()
            );
            throw KeycloakExceptionMapper.map(e, KeycloakOperation.LOGOUT);
        } catch (FeignException e) {
            throw KeycloakExceptionMapper.map(e, KeycloakOperation.LOGOUT);
        }
    }

    // CREATE FORM ========================================================================
    private MultiValueMap<String, String> createTokenIssueForm(String email,
        String currentPassword) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", keycloakProperties.clientId());
        form.add("client_secret", keycloakProperties.clientSecret());
        form.add("username", email);
        form.add("password", currentPassword);
        return form;
    }

    private MultiValueMap<String, String> createTokenReIssueForm(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("client_id", keycloakProperties.clientId());
        form.add("client_secret", keycloakProperties.clientSecret());
        form.add("refresh_token", refreshToken);
        return form;
    }

    private MultiValueMap<String, String> createLogOutForm(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", keycloakProperties.clientId());
        form.add("client_secret", keycloakProperties.clientSecret());
        form.add("refresh_token", refreshToken);
        return form;
    }
}
