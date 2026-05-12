package com.stockflow.domain.dto.invoice;

import com.stockflow.domain.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InvoiceResponseDTO(
    UUID id,
    String invoiceKey,
    String supplierName,
    String supplierCnpj,
    LocalDate purchaseDate,
    BigDecimal totalValue,
    InvoiceStatus status,
    List<InvoiceItemDTO> items,
    LocalDateTime createdAt
) {}
