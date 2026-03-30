package com.dev.minn.profileservice.utils;

import org.springframework.data.neo4j.core.schema.IdGenerator;
import com.github.f4b6a3.uuid.UuidCreator;

public class UuidV7Generator implements IdGenerator<String> {

    @Override
    public String generateId(String primaryLabel, Object entity) {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
