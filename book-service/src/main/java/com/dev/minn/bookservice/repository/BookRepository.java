package com.dev.minn.bookservice.repository;

import com.dev.minn.bookservice.node.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
}
