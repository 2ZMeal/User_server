package com.ezmeal.userservice.application.user.service;

import com.ezmeal.common.exception.types.NotFoundException;
import com.ezmeal.userservice.application.user.dto.UserReadResult;
import com.ezmeal.userservice.common.exception.PolicyException;
import com.ezmeal.userservice.common.exception.code.ResponseCode;
import com.ezmeal.userservice.domain.user.repos.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserReadService {

    private final UserRepository userRepository;

    public String getKeycloakId(String userId) {
        return userRepository.findActive(UUID.fromString(userId))
            .orElseThrow(() -> new NotFoundException(ResponseCode.USER_NOT_FOUND))
            .getKeycloakId();
    }

    public Boolean isEmailExists(String email) {
        return userRepository.findActive(email).isPresent();
    }

    /**
     * Get User data by userId
     * @param userId
     * @return {@link UserReadResult}
     */
    public UserReadResult getUser(UUID userId) {
        return UserReadResult.from(userRepository.findActive(userId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.USER_NOT_FOUND)));
    }

    /**
     * Get paged User detailed data
     * @param pageable
     * @return Paged User detailed data
     */
    public Page<UserReadResult> getUserList(Pageable pageable) {
        return userRepository.findAllActive(pageable).map(UserReadResult::from);
    }

    /**
     * Check is nickname already exists and active
     * @param nickname
     */
    public void isNicknameExists(String nickname) {
        if(!userRepository.isNicknameExists(nickname)) {
            throw new PolicyException(ResponseCode.NICKNAME_ALREADY_EXISTS);
        }
    }
}
