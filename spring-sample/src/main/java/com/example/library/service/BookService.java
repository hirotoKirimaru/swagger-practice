package com.example.library.service;

import com.example.library.domain.ActionType;
import com.example.library.domain.Book;
import com.example.library.domain.BookAction;
import com.example.library.repository.BookActionRepository;
import com.example.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookActionRepository bookActionRepository;

    public BookService(BookRepository bookRepository, BookActionRepository bookActionRepository) {
        this.bookRepository = bookRepository;
        this.bookActionRepository = bookActionRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NoSuchElementException("book not found: " + isbn));
    }

    public Book register(String isbn, String title, String author) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new IllegalArgumentException("already registered: " + isbn);
        }
        return bookRepository.save(new Book(isbn, title, author));
    }

    public void borrow(String isbn, String userId) {
        Book book = findByIsbn(isbn);
        book.borrow(userId);
        bookActionRepository.save(new BookAction(isbn, ActionType.BORROW, userId));
    }

    public void returnBook(String isbn, String userId) {
        Book book = findByIsbn(isbn);
        book.returnBook(userId);
        bookActionRepository.save(new BookAction(isbn, ActionType.RETURN, userId));
    }
}
