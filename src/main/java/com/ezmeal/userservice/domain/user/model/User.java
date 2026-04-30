package com.ezmeal.userservice.domain.user.model;

import com.ezmeal.common.entity.BaseEntity;
import com.ezmeal.common.enums.Role;
import com.ezmeal.userservice.domain.user.code.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_user")
public class User extends BaseEntity{

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

    @Column(name="keycloak_id", updatable = false, nullable = false)
    private String keycloakId;

    @Column(name="nickname", unique = true)
    private String nickname;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;


    // Factory Methods ========================================================================
    public static User create(){
        // TODO : Complete after extends BaseEntity
        User user = new User();
        return user;
    }
}
