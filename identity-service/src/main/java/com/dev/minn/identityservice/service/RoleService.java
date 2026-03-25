package com.dev.minn.identityservice.service;

import com.dev.minn.identityservice.entity.Role;
import com.dev.minn.identityservice.exception.CodeException;
import com.dev.minn.identityservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional(readOnly = true)
public class RoleService {

    RoleRepository roleRepository;

    protected Role findRoleByName(String roleName)  {
        return roleRepository
                .findByName(roleName)
                .orElseThrow(CodeException.ROLE_NOT_FOUND::throwException);
    }
}
