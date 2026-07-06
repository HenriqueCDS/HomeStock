package com.stockflow.mapper;

import com.stockflow.domain.dto.stock.StockMovementResponseDTO;
import com.stockflow.domain.entity.Product;
import com.stockflow.domain.entity.StockMovement;
import com.stockflow.domain.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-06T13:49:37-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Oracle Corporation)"
)
@Component
public class StockMovementMapperImpl implements StockMovementMapper {

    @Override
    public StockMovementResponseDTO toResponse(StockMovement movement) {
        if ( movement == null ) {
            return null;
        }

        UUID productId = null;
        String productName = null;
        UUID id = null;
        MovementType type = null;
        BigDecimal quantity = null;
        BigDecimal unitCost = null;
        BigDecimal stockBefore = null;
        BigDecimal stockAfter = null;
        String reference = null;
        String notes = null;
        LocalDateTime createdAt = null;

        productId = movementProductId( movement );
        productName = movementProductName( movement );
        id = movement.getId();
        type = movement.getType();
        quantity = movement.getQuantity();
        unitCost = movement.getUnitCost();
        stockBefore = movement.getStockBefore();
        stockAfter = movement.getStockAfter();
        reference = movement.getReference();
        notes = movement.getNotes();
        createdAt = movement.getCreatedAt();

        StockMovementResponseDTO stockMovementResponseDTO = new StockMovementResponseDTO( id, productId, productName, type, quantity, unitCost, stockBefore, stockAfter, reference, notes, createdAt );

        return stockMovementResponseDTO;
    }

    private UUID movementProductId(StockMovement stockMovement) {
        if ( stockMovement == null ) {
            return null;
        }
        Product product = stockMovement.getProduct();
        if ( product == null ) {
            return null;
        }
        UUID id = product.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String movementProductName(StockMovement stockMovement) {
        if ( stockMovement == null ) {
            return null;
        }
        Product product = stockMovement.getProduct();
        if ( product == null ) {
            return null;
        }
        String name = product.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
