package com.dev.minn.book.config;

import com.dev.minn.book.node.Book;
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public BeforeConvertCallback<Object> idGenerator() {
        return (entity, collection) -> {
            if (entity instanceof Book book && book.getId() == null) {
                book.setId(UuidCreator.getTimeOrderedEpoch().toString());
            }
            return entity;
        };
    }
}
