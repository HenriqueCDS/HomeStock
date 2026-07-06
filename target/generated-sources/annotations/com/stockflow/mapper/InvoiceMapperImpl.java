package com.stockflow.mapper;

import com.stockflow.domain.dto.invoice.InvoiceItemDTO;
import com.stockflow.domain.dto.invoice.InvoiceResponseDTO;
import com.stockflow.domain.entity.Invoice;
import com.stockflow.domain.entity.InvoiceItem;
import com.stockflow.domain.entity.Product;
import com.stockflow.domain.enums.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-06T13:49:37-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Oracle Corporation)"
)
@Component
public class InvoiceMapperImpl implements InvoiceMapper {

    @Override
    public InvoiceResponseDTO toResponse(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        List<InvoiceItemDTO> items = null;
        UUID id = null;
        String invoiceKey = null;
        String supplierName = null;
        String supplierCnpj = null;
        LocalDate purchaseDate = null;
        BigDecimal totalValue = null;
        InvoiceStatus status = null;
        LocalDateTime createdAt = null;

        items = invoiceItemListToInvoiceItemDTOList( invoice.getItems() );
        id = invoice.getId();
        invoiceKey = invoice.getInvoiceKey();
        supplierName = invoice.getSupplierName();
        supplierCnpj = invoice.getSupplierCnpj();
        purchaseDate = invoice.getPurchaseDate();
        totalValue = invoice.getTotalValue();
        status = invoice.getStatus();
        createdAt = invoice.getCreatedAt();

        InvoiceResponseDTO invoiceResponseDTO = new InvoiceResponseDTO( id, invoiceKey, supplierName, supplierCnpj, purchaseDate, totalValue, status, items, createdAt );

        return invoiceResponseDTO;
    }

    @Override
    public InvoiceItemDTO toItemDTO(InvoiceItem item) {
        if ( item == null ) {
            return null;
        }

        UUID productId = null;
        UUID id = null;
        String productName = null;
        String productEan = null;
        BigDecimal quantity = null;
        BigDecimal unitValue = null;
        BigDecimal totalValue = null;
        String unit = null;

        productId = itemProductId( item );
        id = item.getId();
        productName = item.getProductName();
        productEan = item.getProductEan();
        quantity = item.getQuantity();
        unitValue = item.getUnitValue();
        totalValue = item.getTotalValue();
        unit = item.getUnit();

        InvoiceItemDTO invoiceItemDTO = new InvoiceItemDTO( id, productName, productEan, productId, quantity, unitValue, totalValue, unit );

        return invoiceItemDTO;
    }

    protected List<InvoiceItemDTO> invoiceItemListToInvoiceItemDTOList(List<InvoiceItem> list) {
        if ( list == null ) {
            return null;
        }

        List<InvoiceItemDTO> list1 = new ArrayList<InvoiceItemDTO>( list.size() );
        for ( InvoiceItem invoiceItem : list ) {
            list1.add( toItemDTO( invoiceItem ) );
        }

        return list1;
    }

    private UUID itemProductId(InvoiceItem invoiceItem) {
        if ( invoiceItem == null ) {
            return null;
        }
        Product product = invoiceItem.getProduct();
        if ( product == null ) {
            return null;
        }
        UUID id = product.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
