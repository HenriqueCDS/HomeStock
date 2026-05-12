package com.stockflow.repository;

import com.stockflow.domain.entity.StockMovement;
import com.stockflow.domain.enums.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    Page<StockMovement> findByTenantIdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);

    Page<StockMovement> findByTenantIdAndProductIdOrderByCreatedAtDesc(UUID tenantId, UUID productId, Pageable pageable);

    Page<StockMovement> findByTenantIdAndTypeOrderByCreatedAtDesc(UUID tenantId, MovementType type, Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.tenantId = :tenantId ORDER BY sm.createdAt DESC")
    List<StockMovement> findRecentByTenantId(UUID tenantId, Pageable pageable);
}
