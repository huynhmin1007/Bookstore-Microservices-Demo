package com.dev.minn.notificationservice.repository;

import com.dev.minn.notificationservice.node.EventTemplateMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventTemplateMappingRepository extends MongoRepository<EventTemplateMapping, String> {

    Optional<EventTemplateMapping> findByEventAndIsActiveTrue(String event);
}
