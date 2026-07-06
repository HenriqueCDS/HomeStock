package com.stockflow.mapper;

import com.stockflow.domain.dto.product.ProductRequestDTO;
import com.stockflow.domain.dto.product.ProductResponseDTO;
import com.stockflow.domain.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-06T13:49:38-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(ProductRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Product.ProductBuilder<?, ?> product = Product.builder();

        product.name( dto.name() );
        product.ean( dto.ean() );
        product.category( dto.category() );
        product.unit( dto.unit() );
        product.minimumStock( dto.minimumStock() );

        return product.build();
    }

    @Override
    public ProductResponseDTO toResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String ean = null;
        String category = null;
        String unit = null;
        BigDecimal currentStock = null;
        BigDecimal averageCost = null;
        BigDecimal minimumStock = null;
        boolean active = false;
        LocalDateTime createdAt = null;

        id = product.getId();
        name = product.getName();
        ean = product.getEan();
        category = product.getCategory();
        unit = product.getUnit();
        currentStock = product.getCurrentStock();
        averageCost = product.getAverageCost();
        minimumStock = product.getMinimumStock();
        active = product.isActive();
        createdAt = product.getCreatedAt();

        BigDecimal totalValue = product.getCurrentStock().multiply(product.getAverageCost());
        boolean belowMinimum = product.getMinimumStock() != null && product.getMinimumStock().compareTo(java.math.BigDecimal.ZERO) > 0 && product.getCurrentStock().compareTo(product.getMinimumStock()) <= 0;

        ProductResponseDTO productResponseDTO = new ProductResponseDTO( id, name, ean, category, unit, currentStock, averageCost, minimumStock, totalValue, active, belowMinimum, createdAt );

        return productResponseDTO;
    }

    @Override
    public void updateEntity(ProductRequestDTO dto, Product product) {
        if ( dto == null ) {
            return;
        }

        if ( dto.name() != null ) {
            product.setName( dto.name() );
        }
        if ( dto.ean() != null ) {
            product.setEan( dto.ean() );
        }
        if ( dto.category() != null ) {
            product.setCategory( dto.category() );
        }
        if ( dto.unit() != null ) {
            product.setUnit( dto.unit() );
        }
        if ( dto.minimumStock() != null ) {
            product.setMinimumStock( dto.minimumStock() );
        }
    }
}
