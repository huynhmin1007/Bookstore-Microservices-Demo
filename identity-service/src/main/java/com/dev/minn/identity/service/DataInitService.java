package com.dev.minn.identity.service;

import com.dev.minn.identity.constant.AccountStatus;
import com.dev.minn.identity.entity.Account;
import com.dev.minn.identity.entity.Permission;
import com.dev.minn.identity.entity.Role;
import com.dev.minn.identity.entity.associate.RolePermission;
import com.dev.minn.identity.repository.AccountRepository;
import com.dev.minn.identity.repository.PermissionRepository;
import com.dev.minn.identity.repository.RolePermissionRepository;
import com.dev.minn.identity.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${app.accounts.admin.email}")
    String ADMIN_EMAIL;

    @NonFinal
    @Value("${app.accounts.admin.password}")
    String ADMIN_PASSWORD;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAdminAccount() {
        // 1. Initialize all Roles and Permissions first
        initRolesAndPermissions();

        if (accountRepository.existsByEmail(ADMIN_EMAIL)) return;

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found after init"));

        Account admin = Account.builder()
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .status(AccountStatus.ACTIVE)
                .build();

        admin.addRole(adminRole);

        accountRepository.save(admin);

        log.warn("Admin account created — email: {}", ADMIN_EMAIL);
    }

    @Transactional
    public void initRolesAndPermissions() {
        if(roleRepository.count() > 0) return;

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