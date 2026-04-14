package com.dev.minn.search.event;

import java.time.Instant;
import java.util.List;

public record BookCreatedEvent(
        String id,
        String title,
        List<String> authors,
        String isbn,
        List<String> categories,
        String publisher,
        String description,
        Instant publicationDate,
        Integer pageCount,
        Integer reviews,
        Double rating,
        boolean isActive,
        String coverImage
) {
}
