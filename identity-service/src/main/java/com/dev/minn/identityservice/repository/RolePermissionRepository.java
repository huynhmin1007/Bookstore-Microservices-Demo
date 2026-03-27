package com.dev.minn.identityservice.repository;

import com.dev.minn.identityservice.entity.Permission;
import com.dev.minn.identityservice.entity.Role;
import com.dev.minn.identityservice.entity.associate.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    boolean existsByRoleAndPermission(Role role, Permission permission);
}
