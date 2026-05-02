package com.ezmeal.userservice.application.user.service;

import com.ezmeal.common.exception.types.NotFoundException;
import com.ezmeal.userservice.application.user.dto.UserReadResult;
import com.ezmeal.userservice.common.exception.code.ResponseCode;
import com.ezmeal.userservice.domain.user.repos.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
}
