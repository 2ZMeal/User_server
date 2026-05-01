package com.ezmeal.userservice.infrastructure.client.keycloak.exception;

import com.ezmeal.common.exception.CustomException;
import com.ezmeal.userservice.common.exception.code.ResponseCode;
import com.ezmeal.userservice.infrastructure.client.keycloak.KeycloakOperation;
import feign.FeignException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public final class KeycloakExceptionMapper {

    public static CustomException map(FeignException e, KeycloakOperation operation) {
        return switch(operation) {
            case GET_TOKEN_RESPONSE -> mapGetTokenResponse(e);
            case ISSUE_ADMIN_ACCESS_TOKEN -> mapIssueAdminAccessToken(e);
            case CHANGE_PASSWORD -> mapChangePassword(e);
            case REISSUE_TOKEN -> mapReissueToken(e);
            case LOGOUT -> mapLogout(e);
        };
    }

    private static CustomException mapGetTokenResponse(FeignException e) {
        int status = e.status();

        if (status == 400 || status == 401) {
            return new CustomException(ResponseCode.KEYCLOAK_INVALID_USER_CREDENTIALS);
        }

        if (isKeycloakUnavailable(status)) {
            return new CustomException(ResponseCode.KEYCLOAK_UNAVAILABLE);
        }

        return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
    }

    private static CustomException mapIssueAdminAccessToken(FeignException e) {
        int status = e.status();

        if(isKeycloakUnavailable(status)) {
            return new CustomException(ResponseCode.KEYCLOAK_UNAVAILABLE);
        }

        return new CustomException(ResponseCode.KEYCLOAK_TOKEN_ISSUE_FAILED);
    }

    private static CustomException mapChangePassword(FeignException e) {
        int status = e.status();

        if(isKeycloakUnavailable(status)) {
            return new CustomException(ResponseCode.KEYCLOAK_UNAVAILABLE);
        }

        return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
    }

    private static CustomException mapReissueToken(FeignException e) {
        int status = e.status();

        if(status == 400) {
            return new CustomException(ResponseCode.KEYCLOAK_INVALID_GRANT);
        }

        if(isKeycloakUnavailable(status)) {
            return new CustomException(ResponseCode.KEYCLOAK_UNAVAILABLE);
        }

        return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
    }

    private static CustomException mapLogout(FeignException e) {
        int status = e.status();

        if(isKeycloakUnavailable(status)) {
            return new CustomException(ResponseCode.KEYCLOAK_UNAVAILABLE);
        }

        if (status == 400 || status == 401) {
            return new CustomException(ResponseCode.KEYCLOAK_INVALID_GRANT);
        }

        return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
    }

    private static boolean isKeycloakUnavailable(int status) {
        return status == 503 || status == 504 || status == -1;
    }
}
