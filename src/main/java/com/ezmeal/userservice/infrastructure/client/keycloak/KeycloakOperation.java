package com.ezmeal.userservice.infrastructure.client.keycloak;

public enum KeycloakOperation {
    GET_TOKEN_RESPONSE,
    ISSUE_ADMIN_ACCESS_TOKEN,
    CHANGE_PASSWORD,
    REISSUE_TOKEN,
    LOGOUT,
    CREATE_USER,
    ASSIGN_REALM_ROLE,
    UPDATE_USER_ATTRIBUTE,
    DELETE_USER,
}
