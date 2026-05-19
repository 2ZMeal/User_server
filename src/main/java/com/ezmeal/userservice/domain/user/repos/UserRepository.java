package com.ezmeal.userservice.domain.user.repos;

import com.ezmeal.userservice.domain.user.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    Optional<User> findActive(UUID userId);
    Optional<User> findActive(String email);
    User save(User user);
    User saveAndFlush(User user);
    void delete(User user);
    Page<User> findAllActive(Pageable pageable);

    Boolean isNicknameExists(String nickname);
}
