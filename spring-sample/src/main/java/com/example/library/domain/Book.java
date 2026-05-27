package com.example.library.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(name = "borrower_id")
    private String borrowerId;

    @Version
    private Long version;

    protected Book() {
    }

    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public boolean isBorrowed() {
        return borrowerId != null;
    }

    public void borrow(String userId) {
        if (isBorrowed()) {
            throw new IllegalStateException("already borrowed: " + isbn);
        }
        this.borrowerId = userId;
    }

    public void returnBook(String userId) {
        if (!isBorrowed()) {
            throw new IllegalStateException("not borrowed: " + isbn);
        }
        if (!borrowerId.equals(userId)) {
            throw new IllegalStateException("borrower mismatch: " + isbn);
        }
        this.borrowerId = null;
    }

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getBorrowerId() {
        return borrowerId;
    }
}
