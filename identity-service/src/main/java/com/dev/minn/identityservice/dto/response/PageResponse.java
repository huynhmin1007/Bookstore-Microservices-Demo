package com.dev.minn.identityservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PageResponse<T> {

    int number;
    int size;
    int totalPages;
    int totalElements;
    boolean hasNext;
    boolean hasPrevious;

    List<T> elements;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .number(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getNumberOfElements())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .elements(page.getContent())
                .build();
    }
}
