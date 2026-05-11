package com.dev.minn.bff.mapper;

import com.dev.minn.bff.dto.response.BookDto;
import com.dev.minn.grpc.search.BookSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "authors", source = "authorsList")
    BookDto toDto(BookSummaryResponse response);
    List<BookDto> toDto(List<BookSummaryResponse> responses);
}
