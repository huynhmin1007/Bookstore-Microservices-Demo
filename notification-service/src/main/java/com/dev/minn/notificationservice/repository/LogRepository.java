package com.dev.minn.notificationservice.repository;

import com.dev.minn.notificationservice.node.Log;
import com.dev.minn.notificationservice.node.Template;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends MongoRepository<Log, String> {
}
