package com.dev.minn.identity.repository;

import com.dev.minn.identity.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {

    boolean existsByEmail(String email);
    Optional<Account> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "roles.role"})
    Optional<Account> findDetail_ById(UUID id);
    
}