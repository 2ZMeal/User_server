package com.ezmeal.userservice.domain.user.repos;

import com.ezmeal.userservice.domain.user.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findActive(UUID userId);
    Optional<User> findActive(String email);
    User save(User user);
    void delete(User user);
}
