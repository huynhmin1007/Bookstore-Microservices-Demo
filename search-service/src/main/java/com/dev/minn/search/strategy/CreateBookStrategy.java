package com.dev.minn.search.strategy;

import com.dev.minn.common.strategy.EventHandlerStrategy;
import com.dev.minn.search.document.Book;
import com.dev.minn.search.event.BookCreatedEvent;
import com.dev.minn.search.mapper.BookMapper;
import com.dev.minn.search.repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CreateBookStrategy implements EventHandlerStrategy {

    BookMapper bookMapper;
    ObjectMapper objectMapper;
    BookRepository bookRepository;

    @Override
    public boolean supports(String eventType) {
        return "BOOK_CREATED".equalsIgnoreCase(eventType);
    }

    @Override
    public void handle(JsonNode payload) throws Exception {
        BookCreatedEvent event = objectMapper.treeToValue(payload, BookCreatedEvent.class);
        Book book = bookRepository.save(bookMapper.toBook(event));
        log.info("Book created: {}", book);
    }

    @Override
    public void handle(JsonNode payload, String eventType) throws Exception {

    }
}
