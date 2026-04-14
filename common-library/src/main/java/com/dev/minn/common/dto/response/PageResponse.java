package com.dev.minn.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PageResponse<T> {

    int number;
    int size;
    int totalPages;
    long totalElements;
    boolean hasNext;
    boolean hasPrevious;
    List<T> elements;
}
