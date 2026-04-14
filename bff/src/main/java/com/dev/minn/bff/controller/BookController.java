package com.dev.minn.bff.controller;

import com.dev.minn.bff.dto.request.BookSearchRequest;
import com.dev.minn.bff.dto.response.BookDto;
import com.dev.minn.bff.service.BookService;
import com.dev.minn.common.dto.response.PageResponse;
import com.dev.minn.grpc.search.BookSummaryResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {

    BookService bookService;

    @GetMapping
    @PreAuthorize("@iam.check('book:book:read')")
    public PageResponse<BookDto> searchBooks(
            @Valid @ModelAttribute BookSearchRequest request
    ) {
        return bookService.searchBooks(request);
    }
}
