package com.dev.minn.bff.dto.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookSearchRequest extends SearchRequest {

    List<String> categories;
    Double minRating;

    @AssertTrue(message = "Sort field must be 'title', reviews or 'rating'")
    @Override
    public boolean isValidSortBy() {
        if(!StringUtils.hasText(getSortBy()))
            return true;

        List<String> allowedFields = List.of(
                "title.keyword",
                "reviews",
                "rating"
        );

        return allowedFields.contains(getSortBy());
    }
}
