package com.dev.minn.search.strategy;

import com.dev.minn.common.event.EventEnvelope;
import com.dev.minn.common.strategy.EventHandlerStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UpdateBookStatsStrategy implements EventHandlerStrategy {

    ElasticsearchOperations operations;

    @Override
    public boolean supports(String eventType) { return "BOOK_STATS_UPDATED".equals(eventType); }

    @Override
    public void handle(JsonNode payload) throws Exception {
        throw new UnsupportedOperationException("Chỉ hỗ trợ cập nhật theo lô!");
    }

    @Override
    public void handle(JsonNode payload, String eventType) throws Exception {

    }

    @Override
    public void handleBatch(List<EventEnvelope> envelopes) throws Exception {
        List<UpdateQuery> updateQueries = new ArrayList<>();

        for(EventEnvelope envelope : envelopes) {
            JsonNode payload = envelope.getPayload();
            String bookId = payload.get("bookId").asText();
            int newReviews = payload.get("reviews").asInt();
            double newRating = payload.get("rating").asDouble();

            Document document = Document.create();
            document.put("reviews", newReviews);
            document.put("rating", newRating);

            UpdateQuery updateQuery = UpdateQuery.builder(bookId)
                    .withDocument(document)
                    .withDocAsUpsert(false) // Chỉ update nếu document tồn tại
                    .build();

            updateQueries.add(updateQuery);
        }

        if(!updateQueries.isEmpty()) {
            operations.bulkUpdate(updateQueries, IndexCoordinates.of("books"));
            log.info("Đã bulk update thành công {} bản ghi stats", updateQueries.size());
        }
    }
}
