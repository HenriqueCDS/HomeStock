package com.stockflow.fiscal.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class NfceDTO {
    private String invoiceKey;
    private String supplierName;
    private String supplierCnpj;
    private LocalDate purchaseDate;
    private BigDecimal totalValue;
    private List<NfceItemDTO> items;
}
