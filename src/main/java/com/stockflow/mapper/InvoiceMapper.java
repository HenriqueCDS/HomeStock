package com.stockflow.mapper;

import com.stockflow.domain.dto.invoice.InvoiceItemDTO;
import com.stockflow.domain.dto.invoice.InvoiceResponseDTO;
import com.stockflow.domain.entity.Invoice;
import com.stockflow.domain.entity.InvoiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "items", source = "items")
    InvoiceResponseDTO toResponse(Invoice invoice);

    @Mapping(target = "productId", source = "product.id")
    InvoiceItemDTO toItemDTO(InvoiceItem item);
}
