package com.stockflow.domain.dto.company;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyResponseDTO(
    UUID id,
    String name,
    String cnpj,
    UUID tenantId,
    String email,
    String phone,
    String address,
    boolean active,
    LocalDateTime createdAt
) {}
