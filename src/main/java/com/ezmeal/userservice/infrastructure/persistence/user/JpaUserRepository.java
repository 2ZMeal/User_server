package com.ezmeal.userservice.infrastructure.persistence.user;

import com.ezmeal.userservice.domain.user.model.User;
import com.ezmeal.userservice.domain.user.repos.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, UUID>, UserRepository {

    Optional<User> findByIdAndDeletedAtIsNull(UUID id);
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Page<User> findAllByDeletedAtIsNull(Pageable pageable);

    // Override ===============================================================

    @Override
    default Optional<User> findActive(UUID userId) {
        return findByIdAndDeletedAtIsNull(userId);
    };

    @Override
    default Optional<User> findActive(String email) {return findByEmailAndDeletedAtIsNull(email);}

    @Override
    default Page<User> findAllActive(Pageable pageable) {return findAllByDeletedAtIsNull(pageable);};
}
