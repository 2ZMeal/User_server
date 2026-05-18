package com.ezmeal.userservice.application.user.service;

import com.ezmeal.common.enums.Role;
import com.ezmeal.common.exception.CustomException;
import com.ezmeal.common.exception.types.NotFoundException;
import com.ezmeal.userservice.application.user.dto.SignUpCommand;
import com.ezmeal.userservice.application.user.dto.UpdateUserCommand;
import com.ezmeal.userservice.application.user.dto.UpdateUserResult;
import com.ezmeal.userservice.application.user.event.UserCreatedApplicationEvent;
import com.ezmeal.userservice.application.user.event.UserDeletedApplicationEvent;
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
import org.springframework.dao.DataIntegrityViolationException;
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

        String keycloakId = null;
        User user = null;

        try {
            keycloakId = createKeycloakUser(command);

            user = createLocalUser(command, keycloakId);

            configureKeycloakUser(keycloakId, user.getId(), command.role());

            publishUserCreatedEvent(user);

            return issueToken(command);
        } catch (Exception e) {
            compensateSignUpFailure(keycloakId, user, command.email());
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

        user.disable();
        user.delete(userId.toString());

        userRepository.save(user);
        eventPublisher.publishEvent(UserDeletedApplicationEvent.from(user));
    }

    @Transactional
    public UpdateUserResult updateUser(UUID userId, UpdateUserCommand command) {
        User user = userRepository.findActive(userId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.USER_NOT_FOUND));
        if(!user.getNickname().equals(command.nickname())) {
            userReadService.validateNicknameExists(command.nickname());
        }
        user.update(command);
        userRepository.save(user);
        return UpdateUserResult.from(user);
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

    /**
     * Check is email already exists and active
     * @param email
     */
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
    private void compensateSignUpFailure(String keycloakId, User user, String email) {
        log.warn(
            "UserService :: compensateSignUpFailure() - start. userId={}, keycloakId={}, email={}",
            user != null ? user.getId() : null,
            keycloakId,
            email
        );

        compensateLocalUser(user, email);
        compensateKeycloakUser(keycloakId, email);

        log.warn(
            "UserService :: compensateSignUpFailure() - completed. userId={}, keycloakId={}, email={}",
            user != null ? user.getId() : null,
            keycloakId,
            email
        );
    }

    private void compensateLocalUser(User user, String email) {
        if (user == null || user.getId() == null) {
            return;
        }

        try {
            userRepository.delete(user);

            log.warn(
                "UserService :: compensateLocalUser() - local user deleted. userId={}, email={}",
                user.getId(),
                email
            );
        } catch (Exception e) {
            log.error(
                "UserService :: compensateLocalUser() - failed to delete local user. userId={}, email={}",
                user.getId(),
                email,
                e
            );
        }
    }

    private void compensateKeycloakUser(String keycloakId, String email) {
        if (keycloakId == null || keycloakId.isBlank()) {
            return;
        }

        try {
            keycloakAdminAdapter.deleteUser(keycloakId);

            log.warn(
                "UserService :: compensateKeycloakUser() - keycloak user deleted. keycloakId={}, email={}",
                keycloakId,
                email
            );
        } catch (Exception e) {
            log.error(
                "UserService :: compensateKeycloakUser() - failed to delete keycloak user. keycloakId={}, email={}",
                keycloakId,
                email,
                e
            );
        }
    }

    /**
     * Create User in Keycloak
     * @param command
     * @return {@code String} Keycloak user UUID
     */
    private String createKeycloakUser(SignUpCommand command) {
        String keycloakId = keycloakAdminAdapter.createUser(
            command.toKeycloakCreateUserCommand()
        );

        log.debug(
            "UserService :: signUp() - keycloak user created. keycloakId={}, email={}",
            keycloakId,
            command.email()
        );

        return keycloakId;
    }

    private User createLocalUser(SignUpCommand command, String keycloakId) {
        validateEmailExists(command.email());
        userReadService.validateNicknameExists(command.nickname());

        try {
            User user = userRepository.saveAndFlush(
                User.create(command.toCreateUserCommand(keycloakId))
            );

            log.debug(
                "UserService :: signUp() - local user created. userId={}, keycloakId={}, email={}",
                user.getId(),
                keycloakId,
                command.email()
            );

            return user;
        } catch (DataIntegrityViolationException e) {
            throw mapSignUpUniqueViolation(e);
        }
    }

    private PolicyException mapSignUpUniqueViolation(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();

        if (message != null && message.contains("uk_p_user_email_active")) {
            return new PolicyException(ResponseCode.USER_ALREADY_EXISTS);
        }

        if (message != null && message.contains("uk_p_user_nickname_active")) {
            return new PolicyException(ResponseCode.NICKNAME_ALREADY_EXISTS);
        }

        return new PolicyException(ResponseCode.USER_ALREADY_EXISTS);
    }

    private void configureKeycloakUser(String keycloakId, UUID userId, Role role) {
        assignKeycloakRole(keycloakId, role);
        updateKeycloakServiceUserIdAttribute(keycloakId, userId);
    }

    private void assignKeycloakRole(String keycloakId, Role role) {
        keycloakAdminAdapter.assignRealmRole(keycloakId, role);

        log.debug(
            "UserService :: signUp() - keycloak role assigned. keycloakId={}, role={}",
            keycloakId,
            role
        );
    }

    private void updateKeycloakServiceUserIdAttribute(String keycloakId, UUID userId) {
        keycloakAdminAdapter.updateServiceUserIdAttribute(
            keycloakId,
            userId.toString()
        );

        log.debug(
            "UserService :: signUp() - keycloak service_user_id attribute updated. keycloakId={}, userId={}",
            keycloakId,
            userId
        );
    }

    private void publishUserCreatedEvent(User user) {
        eventPublisher.publishEvent(UserCreatedApplicationEvent.from(user));

        log.debug(
            "UserService :: signUp() - user created application event published. userId={}",
            user.getId()
        );
    }

    private TokenResponse issueToken(SignUpCommand command) {
        TokenResponse tokenResponse = keycloakAuthAdapter
            .getTokenResponse(command.email(), command.password())
            .toResponse();

        log.info(
            "UserService :: signUp() - token issued. email={}",
            command.email()
        );

        return tokenResponse;
    }
}
