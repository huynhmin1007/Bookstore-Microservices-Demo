package com.dev.minn.book.service;

import com.dev.minn.book.config.RabbitMQConfigProps;
import com.dev.minn.book.event.BookCreatedEvent;
import com.dev.minn.book.mapper.BookMapper;
import com.dev.minn.book.node.Book;
import com.dev.minn.book.repository.BookRepository;
import com.dev.minn.common.event.EventEnvelope;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataInitService {

    ObjectMapper objectMapper;
    BookMapper bookMapper;
    RabbitMQConfigProps props;
    RabbitTemplate rabbitTemplate;
    BookRepository bookRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        if (bookRepository.count() > 0) {
            log.info("Dữ liệu sách đã tồn tại, bỏ qua quá trình fake data.");
            return;
        }

        log.info("Bắt đầu tạo dữ liệu giả cho Collection Books...");

        // Khởi tạo Faker. Bạn có thể truyền new Locale("vi") nếu muốn data có tiếng Việt
        Faker faker = new Faker();
        List<Book> fakeBooks = new ArrayList<>();

        for (int i = 0; i < 50; i++) { // Fake 50 cuốn sách
            Book book = new Book();

            book.setTitle(faker.book().title());
            book.setIsbn(faker.code().isbn13());

            // Random 1 đến 2 tác giả
            book.setAuthors(List.of(faker.book().author(), faker.name().fullName()));

            // Sinh một đoạn văn bản ngẫu nhiên làm mô tả
            book.setDescription(faker.lorem().paragraph(3));

            int categoryCount = faker.number().numberBetween(1, 4);
            for (int j = 0; j < categoryCount; j++) {
                book.getCategories().add(faker.book().genre());
            }

            book.setPublisher(faker.book().publisher());

            // Random ngày xuất bản trong vòng 10 năm đổ lại đây
            Instant pubDate = faker.date().past(3650, TimeUnit.DAYS).toInstant();
            book.setPublicationDate(pubDate);

            book.setPageCount(faker.number().numberBetween(100, 1200));

            // Lấy ảnh bìa ngẫu nhiên từ picsum
            book.setCoverImage("https://picsum.photos/seed/" + faker.internet().uuid() + "/400/600");

            // Random điểm đánh giá từ 3.0 đến 5.0
            book.setRating(faker.number().randomDouble(1, 3, 5));
            book.setReviews(faker.number().numberBetween(0, 5000));

            book.setCreatedAt(Instant.now());
            book.setUpdatedAt(Instant.now());

            // 90% sách là active, 10% bị ẩn
            book.setActive(faker.number().numberBetween(1, 100) > 10);

            fakeBooks.add(book);
        }

        // Dùng saveAll() để ghi 1 cục vào MongoDB (Nhanh hơn rất nhiều so với gọi save() 50 lần)
        bookRepository.saveAll(fakeBooks);

        log.info("Đã tạo và lưu thành công {} cuốn sách vào MongoDB!", fakeBooks.size());

        List<BookCreatedEvent> bookEventList = fakeBooks.stream()
                .map(bookMapper::toEvent)
                .map(book -> {
                    book.setQuantity(faker.number().numberBetween(1, 100));
                    book.setPrice(faker.number().randomDouble(2, 50000, 500000));

                    return book;
                })
                .toList();

        bookEventList.forEach(book -> {
            EventEnvelope envelope = EventEnvelope.builder()
                    .eventType("BOOK_CREATED")
                    .source("book-service")
                    .payload(objectMapper.convertValue(book, JsonNode.class))
                    .build();

            rabbitTemplate.convertAndSend(
                    props.getExchanges().getBook(),
                    "book.core.created",
                    envelope
            );
        });
    }
}
