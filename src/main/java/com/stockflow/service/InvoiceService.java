package com.stockflow.service;

import com.stockflow.domain.dto.common.PageResponseDTO;
import com.stockflow.domain.dto.invoice.InvoiceResponseDTO;
import com.stockflow.domain.entity.Invoice;
import com.stockflow.domain.enums.InvoiceStatus;
import com.stockflow.exception.BusinessException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.InvoiceMapper;
import com.stockflow.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<InvoiceResponseDTO> list(UUID tenantId, Pageable pageable) {
        return PageResponseDTO.from(
            invoiceRepository.findByTenantIdAndDeletedAtIsNull(tenantId, pageable)
                .map(invoiceMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public InvoiceResponseDTO getById(UUID tenantId, UUID invoiceId) {
        Invoice invoice = findByTenantAndId(tenantId, invoiceId);
        return invoiceMapper.toResponse(invoice);
    }

    @Transactional
    public void reject(UUID tenantId, UUID invoiceId) {
        Invoice invoice = findByTenantAndId(tenantId, invoiceId);
        if (invoice.getStatus() != InvoiceStatus.FETCHED) {
            throw new BusinessException("Only FETCHED invoices can be rejected", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        invoice.setStatus(InvoiceStatus.REJECTED);
        invoiceRepository.save(invoice);
    }

    @Transactional
    public void delete(UUID tenantId, UUID invoiceId) {
        Invoice invoice = findByTenantAndId(tenantId, invoiceId);
        if (invoice.getStatus() == InvoiceStatus.CONFIRMED) {
            throw new BusinessException("Confirmed invoices cannot be deleted", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        invoice.softDelete();
        invoiceRepository.save(invoice);
    }

    public Invoice findByTenantAndId(UUID tenantId, UUID invoiceId) {
        return invoiceRepository.findByIdAndTenantIdAndDeletedAtIsNull(invoiceId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));
    }
}
