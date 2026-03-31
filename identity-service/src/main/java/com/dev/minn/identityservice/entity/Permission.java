package com.dev.minn.identityservice.entity;

import com.dev.minn.identityservice.entity.associate.RolePermission;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity<UUID> {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    UUID id;

    // It will hold values like: "*:*:*", "identity:account:read", "order:*"
    // service:resource:action
    @Column(unique = true, nullable = false, length = 100)
    String name;

    // E.g., "Grants full administrative access to all services"
    @Column(name = "description")
    String description;

    @OneToMany(mappedBy = "permission")
    @Builder.Default
    Set<RolePermission> roles = new HashSet<>();
}
