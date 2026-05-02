package com.ezmeal.userservice.application.user.service;

import com.ezmeal.common.enums.Role;
import com.ezmeal.common.exception.types.NotFoundException;
import com.ezmeal.userservice.application.user.dto.SignUpCommand;
import com.ezmeal.userservice.application.user.event.UserCreateApplicationEvent;
import com.ezmeal.userservice.common.exception.PolicyException;
import com.ezmeal.userservice.common.exception.code.ResponseCode;
import com.ezmeal.userservice.domain.user.model.User;
import com.ezmeal.userservice.domain.user.repos.UserRepository;
import com.ezmeal.userservice.infrastructure.client.keycloak.KeycloakAdminAdapter;
import com.ezmeal.userservice.infrastructure.client.keycloak.KeycloakAuthAdapter;
import com.ezmeal.userservice.presentation.user.payload.ChangePasswordRequest;
import com.ezmeal.userservice.presentation.user.payload.LogoutRequest;
import com.ezmeal.userservice.presentation.user.payload.ReissueRequest;
import com.ezmeal.userservice.presentation.user.payload.SignInRequest;
import com.ezmeal.userservice.presentation.user.payload.SignUpRequest;
import com.ezmeal.userservice.presentation.user.payload.TokenResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ApplicationEventPublisher eventPublisher;
    private final KeycloakAuthAdapter keycloakAuthAdapter;
    private final KeycloakAdminAdapter keycloakAdminAdapter;
    private final UserReadService userReadService;
    private final UserRepository userRepository;

    /**
     * Authenticates a user via Keycloak and returns an access token.
     * @param request ID/PW
     * @return {@link TokenResponse}
     */
    public TokenResponse signIn(SignInRequest request) {
        return keycloakAuthAdapter.getTokenResponse(request.email(), request.password()).toResponse();
    }

    /**
     * Refreshes the access token via Keycloak using a refresh token.
     * @param request refresh token
     * @return {@link TokenResponse}
     */
    public TokenResponse reissue(ReissueRequest request) {
        return keycloakAuthAdapter.reissueToken(request.refreshToken()).toResponse();
    }

    /**
     * Logs out the user via Keycloak and invalidates the session.
     * @param request refresh token
     */
    public void logout(LogoutRequest request) {
        keycloakAuthAdapter.logout(request.refreshToken());
    }

    /**
     * Change User's password via Keycloak
     * @param userId id from p_user
     * @param email user's email(login_id)
     * @param request {@link ChangePasswordRequest}
     */
    @Transactional
    public void changePassword(String userId, String email, ChangePasswordRequest request) {
        // 1. validate current password
        keycloakAuthAdapter.getTokenResponse(email, request.currentPassword());

        // 2. get user's keycloak id via user id
        String keycloakId = userReadService.getKeycloakId(userId);

        // 3. send password update request as Keycloak admin
        keycloakAdminAdapter.changePassword(keycloakId, request.newPassword());
    }

    /**
     * Handle Sign Up request
     * @param command {@link SignUpRequest}
     */
    @Transactional
    public TokenResponse signUp(SignUpCommand command) {
        validateSignUpRole(command.role());
        validateEmailExists(command.email());

        String keycloakId = null;
        User user = null;

        try {
            keycloakId = keycloakAdminAdapter.createUser(
                command.toKeycloakCreateUserCommand()
            );

            user = userRepository.saveAndFlush(
                User.create(command.toCreateUserCommand(keycloakId))
            );

            keycloakAdminAdapter.assignRealmRole(
                keycloakId,
                command.role()
            );

            keycloakAdminAdapter.updateServiceUserIdAttribute(
                keycloakId,
                user.getId().toString()
            );

            eventPublisher.publishEvent( UserCreateApplicationEvent.from(user));

            return keycloakAuthAdapter.getTokenResponse(command.email(), command.password()).toResponse();
        } catch (Exception e) {
            compensateSignUpFailure(keycloakId, user);
            throw e;
        }
    }

    /**
     * Handle User's withdraw request
     * @param userId
     */
    @Transactional
    public void withdraw(UUID userId) {
        User user = userRepository.findActive(userId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.USER_NOT_FOUND));

        keycloakAdminAdapter.deleteUser(user.getKeycloakId());

        user.delete(userId.toString());

        userRepository.save(user);
    }


    // HELPER METHODS =======================================================

    /**
     * Sign up request's role is allowed only as USER or COMPANY
     * @param role
     */
    private void validateSignUpRole(Role role) {
        if(role == null || role == Role.ADMIN) {
            throw new PolicyException(ResponseCode.FORBIDDEN);
        }
    }

    private void validateEmailExists(String email) {
        if(userReadService.isEmailExists(email)) {
            throw new PolicyException(ResponseCode.USER_ALREADY_EXISTS);
        }
    }

    /**
     * Handle compensation logic for failed sign up logic.
     * @param keycloakId
     * @param user
     */
    private void compensateSignUpFailure(String keycloakId, User user) {
        if(user != null && user.getId() != null) {
            try{
                userRepository.delete(user);
            } catch (Exception e){
                log.error(
                    "UserService :: compensateSignUpFailure() - failed to delete p_user, userId={}",
                    user.getId(),
                    e
                );
            }
        }

        if(keycloakId != null && !keycloakId.isBlank()) {
            try{
                keycloakAdminAdapter.deleteUser(keycloakId);
            } catch (Exception e){
                log.error(
                    "UserService :: compensateSignUpFailure() - failed to delete keycloak user, keycloakId={}",
                    keycloakId,
                    e
                );
            }
        }
    }
}
