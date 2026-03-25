package com.dev.minn.identityservice.service;

import com.dev.minn.identityservice.constant.AccountStatus;
import com.dev.minn.identityservice.entity.Account;
import com.dev.minn.identityservice.entity.Role;
import com.dev.minn.identityservice.exception.CodeException;
import com.dev.minn.identityservice.repository.AccountRepository;
import com.dev.minn.identityservice.repository.RoleRepository;
import io.netty.handler.codec.CodecException;
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
@Transactional(readOnly = true)
public class DataInitService {

    AccountRepository accountRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Transactional
    public void initAdminAccount(String email, String password) {
        if (accountRepository.existsByEmail(email)) return;

        initRole();

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(CodeException.ROLE_NOT_FOUND::throwException);

        Account admin = Account.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .status(AccountStatus.ACTIVE)
                .build();

        admin.addRole(adminRole);
        accountRepository.save(admin);

        log.warn("Admin account created — email: {}", email);
    }

    @Transactional
    public void initRole() {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));
    }
}
