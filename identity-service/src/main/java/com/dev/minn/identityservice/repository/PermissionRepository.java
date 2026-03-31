package com.dev.minn.identityservice.repository;

import com.dev.minn.identityservice.entity.Permission;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    @Query("""
            SELECT p.name FROM AccountRole ar
            JOIN ar.role r
            JOIN RolePermission rp ON rp.role = r
            JOIN rp.permission p
            WHERE ar.account.id = :accountId
            """)
    Set<String> findAllPermissionNamesByAccountId(@Param("accountId") UUID accountId);

    Optional<Permission> findByName(String name);

    @Query("""
            SELECT p FROM Permission p
            JOIN p.roles pr
            WHERE pr.role.id = :roleId
            """)
    Set<Permission> findAllPermissionByRole(@Param("roleId") UUID roleId);
}
