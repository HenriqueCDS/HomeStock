package com.stockflow.domain.dto.product;

public record ProductFilterDTO(
    String name,
    String ean,
    String category,
    Boolean active,
    Boolean belowMinimum
) {}
