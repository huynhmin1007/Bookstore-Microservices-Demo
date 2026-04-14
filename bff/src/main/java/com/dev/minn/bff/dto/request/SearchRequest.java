package com.dev.minn.bff.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public abstract class SearchRequest {

    String keyword;

    @Min(value = 1, message = "Page number must be at least 1")
    int page = 1;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100")
    int size = 10;

    String sortBy;

    @Pattern(regexp = "(?i)^(asc|desc)$", message = "Order must be 'asc' or 'desc'")
    String order="asc";

    public abstract boolean isValidSortBy();
}
