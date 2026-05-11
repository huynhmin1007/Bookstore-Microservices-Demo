package com.dev.minn.bff.service;

import com.dev.minn.bff.dto.request.BookSearchRequest;
import com.dev.minn.bff.dto.response.BookDto;
import com.dev.minn.bff.mapper.BookMapper;
import com.dev.minn.common.dto.response.PageResponse;
import com.dev.minn.grpc.common.PageMetadata;
import com.dev.minn.grpc.search.BookSummaryResponse;
import com.dev.minn.grpc.search.SearchBookRequest;
import com.dev.minn.grpc.search.SearchBookResponse;
import com.dev.minn.grpc.search.SearchGrpcServiceGrpc;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService {

    BookMapper bookMapper;

    @NonFinal
    @GrpcClient("search-service")
    SearchGrpcServiceGrpc.SearchGrpcServiceBlockingStub searchStub;

    public PageResponse<BookDto> searchBooks(BookSearchRequest request) {
        SearchBookRequest.Builder requestBuilder = SearchBookRequest.newBuilder()
                .setPage(request.getPage())
                .setSize(request.getSize())
                .setSortDir(request.getOrder());

        if(StringUtils.hasText(request.getKeyword())) {
            requestBuilder.setKeyword(request.getKeyword());
        }
        if(!CollectionUtils.isEmpty(request.getCategories())) {
            requestBuilder.addAllCategories(request.getCategories());
        }
        if(StringUtils.hasText(request.getSortBy())) {
            requestBuilder.setSortBy(request.getSortBy());
        }
        if(request.getMinRating() != null)
            requestBuilder.setMinRating(request.getMinRating());

        SearchBookResponse grpcResponse = searchStub.searchBooks(requestBuilder.build());

        PageMetadata metadata = grpcResponse.getMeta();

        return PageResponse.<BookDto>builder()
                .number(metadata.getNumber())
                .size(metadata.getSize())
                .totalPages(metadata.getTotalPages())
                .totalElements(metadata.getTotalElements())
                .hasNext(metadata.getHasNext())
                .hasPrevious(metadata.getHasPrevious())
                .elements(bookMapper.toDto(grpcResponse.getElementsList()))
                .build();
    }
}
