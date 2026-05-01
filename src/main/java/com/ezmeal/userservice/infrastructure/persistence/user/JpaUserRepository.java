package com.ezmeal.userservice.infrastructure.persistence.user;

import com.ezmeal.userservice.domain.user.model.User;
import com.ezmeal.userservice.domain.user.repos.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, UUID>, UserRepository {

    Optional<User> findByIdAndDeletedAtIsNull(UUID id);

    // Override ===============================================================

    @Override
    default Optional<User> findActive(UUID userId) {
        return findByIdAndDeletedAtIsNull(userId);
    };
}
