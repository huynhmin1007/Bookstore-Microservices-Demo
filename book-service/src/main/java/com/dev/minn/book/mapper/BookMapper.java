package com.dev.minn.book.mapper;

import com.dev.minn.book.event.BookCreatedEvent;
import com.dev.minn.book.node.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookCreatedEvent toEvent(Book book);
}
