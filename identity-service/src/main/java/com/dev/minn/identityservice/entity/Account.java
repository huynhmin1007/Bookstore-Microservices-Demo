package com.dev.minn.identityservice.entity;

import com.dev.minn.identityservice.constant.AccountStatus;
import com.dev.minn.identityservice.entity.associate.AccountRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Account extends BaseEntity<UUID> {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    UUID id;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "password", nullable = false)
    String password;

    @Builder.Default
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    AccountStatus status = AccountStatus.INACTIVE;

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @Setter(AccessLevel.PRIVATE)
    Set<AccountRole> roles = new HashSet<>();

    public Set<Role> getRolesAsRole() {
        if (roles == null) return Collections.emptySet();
        return roles.stream()
                .map(AccountRole::getRole)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void addRole(Role role) {
        AccountRole accountRole = AccountRole.builder()
                .account(this)
                .role(role)
                .build();
        this.roles.add(accountRole);
    }

    public void removeRole(Role role) {
        roles.removeIf(ar -> ar.getRole().getId().equals(role.getId()));
    }
}
