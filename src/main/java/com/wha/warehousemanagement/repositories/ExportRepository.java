package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.responses.ExportResponse;
import com.wha.warehousemanagement.models.Export;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportRepository extends JpaRepository<Export, Integer> {
    @Query(value = "SELECT e FROM Export e " +
            "JOIN e.exportDetails ed " +
            "JOIN ed.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:exportDate IS NULL OR e.exportDate = :exportDate) " +
            "AND (:customerName IS NULL OR e.customerName LIKE %:customerName%) " +
            "AND (:customerAddress IS NULL OR e.customerAddress LIKE %:customerAddress%) " +
            "AND (:status IS NULL OR e.status = :status)"
    )
    Page<Export> searchExportDetails(@Param("warehouseId") int warehouseId,
                                     @Param("exportDate") String exportDate,
                                     @Param("customerName") String customerName,
                                     @Param("customerAddress") String customerAddress,
                                     @Param("status") String status,
                                     Pageable pageable);
}
