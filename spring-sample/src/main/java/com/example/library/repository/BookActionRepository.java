package com.example.library.repository;

import com.example.library.domain.BookAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookActionRepository extends JpaRepository<BookAction, Long> {

    List<BookAction> findByIsbnOrderByCreatedAtDesc(String isbn);
}
