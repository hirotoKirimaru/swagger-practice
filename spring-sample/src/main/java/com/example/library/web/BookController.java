package com.example.library.web;

import com.example.library.domain.ActionType;
import com.example.library.service.BookService;
import com.example.library.web.dto.BookActionRequest;
import com.example.library.web.dto.BookRegisterRequest;
import com.example.library.web.dto.BookResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Map<String, List<BookResponse>> list() {
        List<BookResponse> books = bookService.findAll().stream()
                .map(BookResponse::from)
                .toList();
        return Map.of("books", books);
    }

    @GetMapping("/{isbn}")
    public BookResponse get(@PathVariable String isbn) {
        return BookResponse.from(bookService.findByIsbn(isbn));
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<BookResponse> register(@PathVariable String isbn,
                                                 @Valid @RequestBody BookRegisterRequest request) {
        if (!isbn.equals(request.isbn())) {
            return ResponseEntity.badRequest().build();
        }
        BookResponse created = BookResponse.from(
                bookService.register(request.isbn(), request.title(), request.author()));
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> delete(@PathVariable String isbn) {
        return ResponseEntity.status(501).build();
    }

    @PostMapping("/{isbn}/actions")
    public ResponseEntity<Void> action(@PathVariable String isbn,
                                       @Valid @RequestBody BookActionRequest request) {
        if (request.type() == ActionType.BORROW) {
            bookService.borrow(isbn, request.userId());
        } else {
            bookService.returnBook(isbn, request.userId());
        }
        return ResponseEntity.ok().build();
    }
}
