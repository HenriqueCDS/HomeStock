package com.stockflow.mapper;

import com.stockflow.domain.dto.stock.StockMovementResponseDTO;
import com.stockflow.domain.entity.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    StockMovementResponseDTO toResponse(StockMovement movement);
}
