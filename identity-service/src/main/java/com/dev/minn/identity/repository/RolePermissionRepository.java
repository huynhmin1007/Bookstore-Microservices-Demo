package com.dev.minn.identity.repository;

import com.dev.minn.identity.entity.Permission;
import com.dev.minn.identity.entity.Role;
import com.dev.minn.identity.entity.associate.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    boolean existsByRoleAndPermission(Role role, Permission permission);
}
