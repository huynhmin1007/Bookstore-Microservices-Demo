package com.dev.minn.identityservice.repository;

import com.dev.minn.identityservice.entity.associate.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
}
