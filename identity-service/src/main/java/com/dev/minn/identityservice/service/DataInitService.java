package com.dev.minn.identityservice.service;

import com.dev.minn.identityservice.constant.AccountStatus;
import com.dev.minn.identityservice.entity.Account;
import com.dev.minn.identityservice.entity.Permission;
import com.dev.minn.identityservice.entity.Role;
import com.dev.minn.identityservice.entity.associate.AccountRole;
import com.dev.minn.identityservice.entity.associate.RolePermission;
import com.dev.minn.identityservice.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DataInitService {

    AccountRepository accountRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;
    AccountRoleRepository accountRoleRepository; // Since we use a custom join table
    PasswordEncoder passwordEncoder;

    @Transactional
    public void initAdminAccount(String email, String password) {
        // 1. Initialize all Roles and Permissions first
        initRolesAndPermissions();

        if (accountRepository.existsByEmail(email)) return;

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found after init"));

        Account admin = Account.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                 .status(AccountStatus.ACTIVE)
                .build();

        accountRepository.save(admin);

        AccountRole accountRole = AccountRole.builder()
                .account(admin)
                .role(adminRole)
                .build();
        accountRoleRepository.save(accountRole);

        log.warn("Admin account created — email: {}", email);
    }

    @Transactional
    public void initRolesAndPermissions() {
        Permission rootPermission = createPermissionIfNotFound("*:*:*", "Absolute System Administrator Access");
        Permission readOwnProfile = createPermissionIfNotFound("identity:account:read-self", "Read own profile data");
        Permission updateOwnProfile = createPermissionIfNotFound("identity:account:update-self", "Update own profile data");
        Permission readCatalog = createPermissionIfNotFound("catalog:book:read", "Browse the public book catalog");

        Role adminRole = createRoleIfNotFound("ADMIN");
        Role userRole = createRoleIfNotFound("USER");

        grantPermissionIfNotFound(adminRole, rootPermission);

        grantPermissionIfNotFound(userRole, readOwnProfile);
        grantPermissionIfNotFound(userRole, updateOwnProfile);
        grantPermissionIfNotFound(userRole, readCatalog);
    }

    private Permission createPermissionIfNotFound(String name, String description) {
        return permissionRepository.findByName(name).orElseGet(() -> {
            Permission p = Permission.builder().name(name).description(description).build();
            return permissionRepository.save(p);
        });
    }

    private Role createRoleIfNotFound(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role r = Role.builder().name(name).build();
            return roleRepository.save(r);
        });
    }

    private void grantPermissionIfNotFound(Role role, Permission permission) {
        boolean alreadyGranted = rolePermissionRepository.existsByRoleAndPermission(role, permission);
        if (!alreadyGranted) {
            RolePermission rp = RolePermission.builder()
                    .role(role)
                    .permission(permission)
                    .build();
            rolePermissionRepository.save(rp);
        }
    }
}