package com.dev.minn.common.messaging.repository;

import com.dev.minn.common.messaging.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

    boolean existsByMessageIdAndConsumerName(String messageId, String consumerName);
}
