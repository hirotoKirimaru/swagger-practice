package com.example.library.batch;

import com.example.library.domain.Book;
import com.example.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BookItemProcessor implements ItemProcessor<BookCsvRecord, Book> {

    private static final Logger log = LoggerFactory.getLogger(BookItemProcessor.class);

    private final BookRepository bookRepository;

    public BookItemProcessor(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book process(BookCsvRecord item) {
        if (bookRepository.existsByIsbn(item.getIsbn())) {
            log.debug("skip duplicated isbn: {}", item.getIsbn());
            return null;
        }
        return new Book(item.getIsbn(), item.getTitle(), item.getAuthor());
    }
}
