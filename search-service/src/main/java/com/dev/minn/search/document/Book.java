package com.dev.minn.search.document;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "books")
@Setting(settingPath = "elastic-settings.json")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {

    @Id
    String id;

    @MultiField(
            mainField = @Field(
                    type = FieldType.Text,
                    analyzer = "autocomplete_analyzer",
                    searchAnalyzer = "autocomplete_search_analyzer"
            ),
            otherFields = {@InnerField(suffix = "keyword", type = FieldType.Keyword)}
    )
    String title;

    @MultiField(
            mainField = @Field(
                    type = FieldType.Text,
                    analyzer = "autocomplete_analyzer",
                    searchAnalyzer = "autocomplete_search_analyzer"
            ),
            otherFields = {@InnerField(suffix = "keyword", type = FieldType.Keyword)}
    )
    List<String> authors;

    @Field(type = FieldType.Keyword)
    String isbn;

    @Field(type = FieldType.Keyword)
    List<String> categories = new ArrayList<>();

    @Field(type = FieldType.Keyword)
    String publisher;

    @Field(type = FieldType.Text)
    String description;

    @Field(type = FieldType.Date)
    Instant publicationDate;

    @Field(type = FieldType.Integer)
    Integer pageCount;

    @Field(type = FieldType.Integer)
    Integer reviews;

    @Field(type = FieldType.Double)
    Double rating;

    @Field(type = FieldType.Boolean)
    boolean isActive;

    @Field(type = FieldType.Keyword, index = false)
    String coverImage;

    @Field(type = FieldType.Integer)
    int quantity;

    @Field(
            type = FieldType.Scaled_Float,
            scalingFactor = 100
    )
    double price;
}
