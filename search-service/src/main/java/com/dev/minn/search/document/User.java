package com.dev.minn.search.document;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = "users")
@Setting(settingPath = "elastic-settings.json")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class User {

    @Id
    String id;

    @Field(type = FieldType.Keyword)
    String accountId;

    @Field(type = FieldType.Keyword)
    String profileId;

    @Field(type = FieldType.Keyword)
    String email;

    @MultiField(
            mainField = @Field(
                    type = FieldType.Text,
                    analyzer = "autocomplete_analyzer",
                    searchAnalyzer = "autocomplete_search_analyzer"
            ),
            otherFields = {@InnerField(suffix = "keyword", type = FieldType.Keyword)}
    )
    String fullName;
}
