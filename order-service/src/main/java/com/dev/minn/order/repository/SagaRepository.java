package com.dev.minn.order.repository;

import com.dev.minn.order.entity.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SagaRepository extends JpaRepository<SagaInstance, String> {
}
