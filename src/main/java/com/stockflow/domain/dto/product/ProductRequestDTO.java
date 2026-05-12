package com.stockflow.domain.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequestDTO(
    @NotBlank @Size(min = 1, max = 255) String name,
    @Size(max = 14) String ean,
    String category,
    @Size(max = 20) String unit,
    @PositiveOrZero BigDecimal minimumStock
) {}
