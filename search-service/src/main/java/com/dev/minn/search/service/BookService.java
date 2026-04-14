package com.dev.minn.search.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.dev.minn.grpc.search.SearchBookRequest;
import com.dev.minn.search.document.Book;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class BookService {

    ElasticsearchOperations operations;

    public SearchPage<Book> searchBooks(SearchBookRequest request) {

        List<Query> filterQueries = new ArrayList<>();

//        filterQueries.add(Query.of(q -> q
//                .term(t -> t
//                        .field("isActive")
//                        .value(true)
//                )
//        ));

        if (!request.getCategoriesList().isEmpty()) {
            List<FieldValue> categoryValues = request.getCategoriesList().stream()
                    .map(FieldValue::of)
                    .toList();

            filterQueries.add(Query.of(q -> q
                    .terms(t -> t
                            .field("categories")
                            .terms(termsField -> termsField.value(categoryValues))
                    )
            ));
        }

        if (request.getMinRating() > 0) {
            filterQueries.add(Query.of(q -> q
                    .range(r -> r
                            .number(n -> n
                                    .field("rating")
                                    .gte(request.getMinRating())
                            )
                    )
            ));
        }

        Query keywordQuery = null;
        if (StringUtils.hasText(request.getKeyword())) {
            keywordQuery = Query.of(q -> q
                    .multiMatch(m -> m
                            .query(request.getKeyword())
                            // Boosting: Ưu tiên Title -> Author -> Description
                            .fields("title^3", "authors^2", "description")
                            // Bật tính năng tìm kiếm sai chính tả
                            .fuzziness("AUTO")
                    )
            );
        }

        Query finalQuery;
        if (keywordQuery != null) {
            Query finalKeywordQuery = keywordQuery;
            finalQuery = Query.of(q -> q
                    .bool(b -> b
                            .filter(filterQueries)
                            .must(finalKeywordQuery)
                    )
            );
        } else {
            finalQuery = Query.of(q -> q
                    .bool(b -> b.filter(filterQueries))
            );
        }

        int page = Math.max(0, request.getPage() - 1);
        int size = request.getSize() > 0 ? request.getSize() : 20;

        Sort sort = Sort.unsorted();
        if (!StringUtils.hasText(request.getKeyword())) {
            String sortBy = StringUtils.hasText(request.getSortBy()) ? request.getSortBy() : "publicationDate";
            Sort.Direction dir = "asc".equalsIgnoreCase(request.getSortDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(dir, sortBy);
        }

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(pageRequest)
                .build();

        SearchHits<Book> searchHits = operations.search(nativeQuery, Book.class);
        return SearchHitSupport.searchPageFor(searchHits, pageRequest);
    }
}