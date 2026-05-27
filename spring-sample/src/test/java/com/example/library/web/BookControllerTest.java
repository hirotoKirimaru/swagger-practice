package com.example.library.web;

import com.example.library.repository.BookActionRepository;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.batch.job.enabled=false")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookActionRepository bookActionRepository;

    @BeforeEach
    void setUp() {
        bookActionRepository.deleteAll();
        bookRepository.deleteAll();
        bookService.register("9784101010137", "吾輩は猫である", "夏目漱石");
    }

    @Test
    void 一覧取得() throws Exception {
        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].isbn").value("9784101010137"));
    }

    @Test
    void 借用と返却ができる() throws Exception {
        mockMvc.perform(post("/api/v1/books/9784101010137/actions")
                        .contentType("application/json")
                        .content("{\"type\":\"BORROW\",\"userId\":\"user-1\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/books/9784101010137"))
                .andExpect(jsonPath("$.borrowerId").value("user-1"));

        mockMvc.perform(post("/api/v1/books/9784101010137/actions")
                        .contentType("application/json")
                        .content("{\"type\":\"RETURN\",\"userId\":\"user-1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void バリデーションエラーは400() throws Exception {
        mockMvc.perform(put("/api/v1/books/9784999999999")
                        .contentType("application/json")
                        .content("{\"isbn\":\"123\",\"title\":\"\",\"author\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
