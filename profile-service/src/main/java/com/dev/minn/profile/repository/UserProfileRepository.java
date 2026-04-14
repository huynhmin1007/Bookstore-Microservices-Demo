package com.dev.minn.profile.repository;

import com.dev.minn.profile.domain.UserProfile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends Neo4jRepository<UserProfile, String> {

    Optional<UserProfile> findByAccountId(String accountId);
}
