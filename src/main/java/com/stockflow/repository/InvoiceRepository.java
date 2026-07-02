package com.stockflow.repository;

import com.stockflow.domain.entity.Invoice;
import com.stockflow.domain.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByInvoiceKeyAndTenantId(String invoiceKey, UUID tenantId);

    boolean existsByInvoiceKeyAndTenantId(String invoiceKey, UUID tenantId);

    Page<Invoice> findByTenantIdAndDeletedAtIsNull(UUID tenantId, Pageable pageable);

    Page<Invoice> findByTenantIdAndStatusAndDeletedAtIsNull(UUID tenantId, InvoiceStatus status, Pageable pageable);

    Optional<Invoice> findByIdAndTenantIdAndDeletedAtIsNull(UUID id, UUID tenantId);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.tenantId = :tenantId AND i.deletedAt IS NULL")
    long countByTenantId(UUID tenantId);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.tenantId = :tenantId AND i.status IN ('PENDING', 'FETCHED') AND i.deletedAt IS NULL")
    long countPendingByTenantId(UUID tenantId);
}
