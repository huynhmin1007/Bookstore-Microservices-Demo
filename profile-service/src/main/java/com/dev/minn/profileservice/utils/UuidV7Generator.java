package com.dev.minn.profileservice.utils;

import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.data.neo4j.core.schema.IdGenerator;

public class UuidV7Generator implements IdGenerator<String> {

    @Override
    public String generateId(String primaryLabel, Object entity) {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
