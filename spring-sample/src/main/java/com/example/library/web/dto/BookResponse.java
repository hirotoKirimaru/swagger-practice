package com.example.library.web.dto;

import com.example.library.domain.Book;

public record BookResponse(String isbn, String title, String author, String borrowerId) {

    public static BookResponse from(Book book) {
        return new BookResponse(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getBorrowerId());
    }
}
