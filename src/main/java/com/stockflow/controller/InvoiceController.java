package com.stockflow.controller;

import com.stockflow.domain.dto.common.ApiResponseDTO;
import com.stockflow.domain.dto.common.PageResponseDTO;
import com.stockflow.domain.dto.invoice.InvoiceResponseDTO;
import com.stockflow.security.JwtTokenProvider;
import com.stockflow.service.InvoiceService;
import com.stockflow.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Invoice history and management")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    @Operation(summary = "List invoices with pagination")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<InvoiceResponseDTO>>> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        HttpServletRequest request) {
        var tenantId = SecurityUtils.getCurrentTenantId(jwtTokenProvider, request);
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponseDTO.ok(invoiceService.list(tenantId, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ApiResponseDTO<InvoiceResponseDTO>> getById(
        @PathVariable UUID id,
        HttpServletRequest request) {
        var tenantId = SecurityUtils.getCurrentTenantId(jwtTokenProvider, request);
        return ResponseEntity.ok(ApiResponseDTO.ok(invoiceService.getById(tenantId, id)));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject invoice")
    public ResponseEntity<ApiResponseDTO<Void>> reject(@PathVariable UUID id, HttpServletRequest request) {
        var tenantId = SecurityUtils.getCurrentTenantId(jwtTokenProvider, request);
        invoiceService.reject(tenantId, id);
        return ResponseEntity.ok(ApiResponseDTO.ok("Invoice rejected", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete invoice (soft delete, not allowed for CONFIRMED)")
    public ResponseEntity<Void> delete(@PathVariable UUID id, HttpServletRequest request) {
        var tenantId = SecurityUtils.getCurrentTenantId(jwtTokenProvider, request);
        invoiceService.delete(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}
