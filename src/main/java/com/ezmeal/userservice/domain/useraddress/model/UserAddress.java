package com.ezmeal.userservice.domain.useraddress.model;

import com.ezmeal.common.entity.BaseEntity;
import com.ezmeal.userservice.domain.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_user_address")
public class UserAddress extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", updatable = false, nullable = false)
    private User user;

    @Column(name="road_name_address", nullable = false, length=256)
    private String roadNameAddress;

    @Column(name="lot_number_address", length=256)
    private String lotNumberAddress;

    @Column(name="zip_code", nullable = false, length = 10)
    private String zipCode;

    @Column(name="is_default", nullable = false)
    private boolean isDefault;

    // Factory Methods ====================================================
    public static UserAddress create() {
        // TODO : Complete after extends BaseEntity
        UserAddress userAddress = new UserAddress();
        return userAddress;
    }
}
