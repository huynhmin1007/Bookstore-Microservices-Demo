package com.dev.minn.search.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.dev.minn.common.dto.response.PageResponse;
import com.dev.minn.grpc.common.PageMetadata;
import com.dev.minn.grpc.search.BookSummaryResponse;
import com.dev.minn.grpc.search.SearchBookRequest;
import com.dev.minn.grpc.search.SearchBookResponse;
import com.dev.minn.grpc.search.SearchGrpcServiceGrpc;
import com.dev.minn.search.document.Book;
import com.dev.minn.search.service.BookService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.elasticsearch.core.SearchPage;

import java.util.List;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class SearchGrpcServer extends SearchGrpcServiceGrpc.SearchGrpcServiceImplBase {

    private final BookService bookService;

    @Override
    public void searchBooks(SearchBookRequest request, StreamObserver<SearchBookResponse> responseObserver) {
        log.info("BFF yêu cầu tìm kiếm sách. Keyword: '{}', Category: {}, Page: {}",
                request.getKeyword(), request.getCategoriesList(), request.getPage());

        try {
            SearchPage<Book> resultPage = bookService.searchBooks(request);

            List<BookSummaryResponse> bookProtos = resultPage.getContent().stream()
                    .map(hit -> {
                        Book book = hit.getContent();
                        return BookSummaryResponse.newBuilder()
                                .setId(book.getId())
                                .setTitle(book.getTitle())
                                .setCoverImage(book.getCoverImage() != null ? book.getCoverImage() : "")
                                .addAllAuthors(book.getAuthors() != null ? book.getAuthors() : List.of())
                                .setRating(book.getRating() != null ? book.getRating() : 0.0)
                                .setReviews(book.getReviews() != null ? book.getReviews() : 0)
                                .build();
                    })
                    .toList();

            PageMetadata pageMetadata = PageMetadata.newBuilder()
                    .setNumber(resultPage.getNumber())
                    .setSize(resultPage.getSize())
                    .setTotalPages(resultPage.getTotalPages())
                    .setTotalElements(resultPage.getTotalElements())
                    .setHasNext(resultPage.hasNext())
                    .setHasPrevious(resultPage.hasPrevious())
                    .build();

            SearchBookResponse response = SearchBookResponse.newBuilder()
                    .setMeta(pageMetadata)
                    .addAllElements(bookProtos)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Lỗi hệ thống khi truy vấn Elasticsearch: ", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Lỗi tìm kiếm: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
