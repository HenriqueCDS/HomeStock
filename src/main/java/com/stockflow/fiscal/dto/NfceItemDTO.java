package com.stockflow.fiscal.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class NfceItemDTO {
    private String name;
    private String ean;
    private BigDecimal quantity;
    private BigDecimal unitValue;
    private BigDecimal totalValue;
    private String unit;
}
