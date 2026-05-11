package com.stockflow.domain.dto.dashboard;

import com.stockflow.domain.enums.MovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RecentMovementDTO(
    UUID id,
    String productName,
    MovementType type,
    BigDecimal quantity,
    LocalDateTime createdAt
) {}
