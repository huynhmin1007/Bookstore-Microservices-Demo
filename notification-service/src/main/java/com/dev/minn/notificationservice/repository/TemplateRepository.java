package com.dev.minn.notificationservice.repository;

import com.dev.minn.notificationservice.node.Template;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends MongoRepository<Template, String> {

    Optional<Template> findByCode(String code);
}
