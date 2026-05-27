package com.example.library.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record BookRegisterRequest(
        @NotBlank @Pattern(regexp = "\\d{13}", message = "isbn must be 13 digits") String isbn,
        @NotBlank String title,
        @NotBlank String author
) {
}
