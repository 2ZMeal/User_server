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
            case CREATE_USER -> mapCreateUser(e);
            case ASSIGN_REALM_ROLE -> mapAssignRealmRole(e);
            case UPDATE_USER_ATTRIBUTE -> mapUpdateUserAttribute(e);
            case DELETE_USER -> mapDeleteUser(e);
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

    private static CustomException mapCreateUser(FeignException e) {
        int status = e.status();

        if(isKeycloakUnavailable(status)) {
            return new CustomException(ResponseCode.KEYCLOAK_UNAVAILABLE);
        }

        if(status == 409) {
            return new CustomException(ResponseCode.USER_ALREADY_EXISTS);
        }

        if(status == 400) {
            return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }

        if(status == 401 || status == 403) {
            return new CustomException(ResponseCode.KEYCLOAK_TOKEN_ISSUE_FAILED);
        }

        return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
    }

    private static CustomException mapAssignRealmRole(FeignException e) {
        int status = e.status();

        if (status == 404) {
            return new CustomException(ResponseCode.KEYCLOAK_ROLE_NOT_FOUND);
        }

        if (status == 401 || status == 403) {
            return new CustomException(ResponseCode.KEYCLOAK_TOKEN_ISSUE_FAILED);
        }

        if (isKeycloakUnavailable(status)) {
            return new CustomException(ResponseCode.KEYCLOAK_UNAVAILABLE);
        }

        return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
    }

    private static CustomException mapGetUser(FeignException e) {
        int status = e.status();

        if (status == 404) {
            return new CustomException(ResponseCode.KEYCLOAK_USER_NOT_FOUND);
        }

        if (status == 401 || status == 403) {
            return new CustomException(ResponseCode.KEYCLOAK_TOKEN_ISSUE_FAILED);
        }

        if (isKeycloakUnavailable(status)) {
            return new CustomException(ResponseCode.KEYCLOAK_UNAVAILABLE);
        }

        return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
    }

    private static CustomException mapUpdateUserAttribute(FeignException e) {
        int status = e.status();

        if (status == 404) {
            return new CustomException(ResponseCode.KEYCLOAK_USER_NOT_FOUND);
        }

        if (status == 400) {
            return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
        }

        if (status == 401 || status == 403) {
            return new CustomException(ResponseCode.KEYCLOAK_TOKEN_ISSUE_FAILED);
        }

        if (isKeycloakUnavailable(status)) {
            return new CustomException(ResponseCode.KEYCLOAK_UNAVAILABLE);
        }

        return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
    }

    private static CustomException mapDeleteUser(FeignException e) {
        int status = e.status();

        if (status == 404) {
            return new CustomException(ResponseCode.KEYCLOAK_USER_NOT_FOUND);
        }

        if (status == 401 || status == 403) {
            return new CustomException(ResponseCode.KEYCLOAK_TOKEN_ISSUE_FAILED);
        }

        if (isKeycloakUnavailable(status)) {
            return new CustomException(ResponseCode.KEYCLOAK_UNAVAILABLE);
        }

        return new CustomException(ResponseCode.KEYCLOAK_REQUEST_FAILED);
    }
}
