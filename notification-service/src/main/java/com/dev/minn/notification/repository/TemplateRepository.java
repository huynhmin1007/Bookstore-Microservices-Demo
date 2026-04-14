package com.dev.minn.notification.repository;

import com.dev.minn.notification.node.Template;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends MongoRepository<Template, String> {

    Optional<Template> findByCode(String code);
    boolean existsByCode(String code);
}
