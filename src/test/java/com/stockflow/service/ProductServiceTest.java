package com.stockflow.service;

import com.stockflow.domain.dto.product.ProductRequestDTO;
import com.stockflow.domain.dto.product.ProductResponseDTO;
import com.stockflow.domain.entity.Product;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.ProductMapper;
import com.stockflow.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepository;
    @Mock ProductMapper productMapper;

    @InjectMocks ProductService productService;

    private UUID tenantId;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        testProduct = Product.builder()
            .tenantId(tenantId)
            .name("Test Product")
            .ean("7891234567890")
            .currentStock(BigDecimal.TEN)
            .averageCost(new BigDecimal("5.50"))
            .build();
        try {
            var idField = testProduct.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testProduct, UUID.randomUUID());
        } catch (Exception ignored) {}
    }

    @Test
    void create_shouldThrowWhenEanAlreadyExists() {
        when(productRepository.findByTenantIdAndEanAndDeletedAtIsNull(any(), anyString()))
            .thenReturn(Optional.of(testProduct));

        ProductRequestDTO request = new ProductRequestDTO("Product", "7891234567890", null, "UN", BigDecimal.ZERO);

        assertThatThrownBy(() -> productService.create(tenantId, request))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void create_shouldCreateProductSuccessfully() {
        ProductRequestDTO request = new ProductRequestDTO("New Product", "1234567890123", null, "UN", BigDecimal.ZERO);
        ProductResponseDTO expectedResponse = new ProductResponseDTO(
            UUID.randomUUID(), "New Product", "1234567890123", null, "UN",
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            true, false, null);

        when(productRepository.findByTenantIdAndEanAndDeletedAtIsNull(any(), anyString()))
            .thenReturn(Optional.empty());
        when(productMapper.toEntity(any())).thenReturn(testProduct);
        when(productRepository.save(any())).thenReturn(testProduct);
        when(productMapper.toResponse(any())).thenReturn(expectedResponse);

        ProductResponseDTO result = productService.create(tenantId, request);
        assertThat(result.name()).isEqualTo("New Product");
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(productRepository.findByIdAndTenantIdAndDeletedAtIsNull(any(), any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(tenantId, UUID.randomUUID()))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldSoftDeleteProduct() {
        when(productRepository.findByIdAndTenantIdAndDeletedAtIsNull(any(), any()))
            .thenReturn(Optional.of(testProduct));
        when(productRepository.save(any())).thenReturn(testProduct);

        productService.delete(tenantId, UUID.randomUUID());

        verify(productRepository).save(argThat(p -> p.isDeleted() && !p.isActive()));
    }

    @Test
    void updateAverageCost_shouldCalculateCorrectly() {
        Product product = Product.builder()
            .currentStock(new BigDecimal("10"))
            .averageCost(new BigDecimal("5.00"))
            .build();

        product.updateAverageCost(new BigDecimal("10"), new BigDecimal("7.00"));

        assertThat(product.getCurrentStock()).isEqualByComparingTo("20");
        assertThat(product.getAverageCost()).isEqualByComparingTo("6.0000");
    }
}
