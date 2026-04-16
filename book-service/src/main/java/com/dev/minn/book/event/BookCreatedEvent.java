package com.dev.minn.book.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BookCreatedEvent {
    String id;
    String title;
    List<String> authors;
    String isbn;
    List<String> categories;
    String publisher;
    String description;
    Instant publicationDate;
    Integer pageCount;
    Integer reviews;
    Double rating;
    boolean isActive;
    String coverImage;
    int quantity;
    double price;
}
