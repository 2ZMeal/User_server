package com.ezmeal.userservice.domain.user.repos;

import com.ezmeal.userservice.domain.user.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findActive(UUID userId);
}
