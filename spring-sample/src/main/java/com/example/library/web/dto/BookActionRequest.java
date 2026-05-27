package com.example.library.web.dto;

import com.example.library.domain.ActionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record BookActionRequest(@NotNull ActionType type, @NotBlank String userId) {
}
