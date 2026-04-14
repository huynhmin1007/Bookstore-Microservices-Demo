package com.dev.minn.identity.entity;

import com.dev.minn.identity.entity.associate.RolePermission;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "roles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Role extends BaseEntity<UUID> {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    UUID id;

    @Column(name = "name", unique = true, nullable = false)
    String name;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "role",
            orphanRemoval = true
    )
    @Builder.Default
    Set<RolePermission> permissions = new HashSet<>();

    public Set<Permission> getPermissionsAsPermission() {
        return permissions.stream()
                .map(RolePermission::getPermission)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void addPermission(Permission permission) {
        RolePermission rolePermission = RolePermission.builder()
                .role(this)
                .permission(permission)
                .build();
        this.permissions.add(rolePermission);
    }

    public void removePermission(Permission permission) {
        permissions.removeIf(rp -> rp.getPermission().getId().equals(permission.getId()));
    }

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
