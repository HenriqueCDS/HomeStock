package com.stockflow.domain.dto.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public record TopProductDTO(
    UUID id,
    String name,
    BigDecimal currentStock,
    BigDecimal totalValue
) {}
