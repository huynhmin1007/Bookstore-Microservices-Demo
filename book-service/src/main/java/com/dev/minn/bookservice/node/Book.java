package com.dev.minn.bookservice.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "books")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Book {

    @Id
    @Field(value = "id")
    String id;

    @Indexed
    @Field(value = "title")
    String title;

    // ISBN (Mã số sách chuẩn quốc tế)
    @Indexed
    @Field(value = "isbn")
    String isbn;

    @Field(value = "authors")
    List<String> authors;

    @Field(value = "description")
    String description;

    @Field(value = "publisher")
    String publisher;

    @Field(value = "publication_date")
    Instant publicationDate;

    @Field(value = "page_count")
    Integer pageCount;

    @Field(value = "cover_image")
    String coverImage;

    @Field(value = "rating")
    Double rating;

    @Field(value = "reviews")
    Integer reviews;

    @CreatedDate
    @Field(value = "created_at")
    Instant createdAt;

    @LastModifiedDate
    @Field(value = "updated_at")
    Instant updatedAt;

    @Field(value = "is_active")
    boolean isActive;
}
