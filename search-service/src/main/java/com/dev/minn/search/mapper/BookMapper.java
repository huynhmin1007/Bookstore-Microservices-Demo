package com.dev.minn.search.mapper;

import com.dev.minn.search.document.Book;
import com.dev.minn.search.event.BookCreatedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toBook(BookCreatedEvent event);
}
