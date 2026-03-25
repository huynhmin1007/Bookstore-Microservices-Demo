package com.dev.minn.identityservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

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

    /**
     * Unique permission name, e.g. "READ_USERS", "DELETE_ORDER".
     * Convention: {ACTION}_{RESOURCE} — all uppercase.
     */
    @Column(name = "name", unique = true, nullable = false)
    String name;

    @Column(name = "description")
    String description;
}
