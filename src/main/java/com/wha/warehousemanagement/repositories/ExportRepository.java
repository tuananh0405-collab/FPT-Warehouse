package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Export;
import com.wha.warehousemanagement.models.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportRepository extends JpaRepository<Export, Integer> {
//    @Query(value = "SELECT e FROM Export e " +
//            "JOIN e.exportDetails ed " +
//            "JOIN ed.zone z " +
//            "JOIN z.warehouse w " +
//            "WHERE w.id = :warehouseId " +
//            "AND (:exportDate IS NULL OR e.exportDate = :exportDate) " +
//            "AND (:customerName IS NULL OR e.customerName LIKE %:customerName%) " +
//            "AND (:customerAddress IS NULL OR e.customerAddress LIKE %:customerAddress%) " +
//            "AND (:status IS NULL OR e.status = :status)"
//    )
//    Page<Export> searchExportDetails(@Param("warehouseId") int warehouseId,
//                                     @Param("exportDate") String exportDate,
//                                     @Param("customerName") String customerName,
//                                     @Param("customerAddress") String customerAddress,
//                                     @Param("status") String status,
//                                     Pageable pageable);

    boolean existsByTransferKey(String transferKey);

    @Query("SELECT e FROM Export e WHERE (e.warehouseFrom.id = :warehouseId) " +
            "ORDER BY " +
            "CASE " +
            "WHEN e.status = 'REQUESTING' THEN 1 " +
            "WHEN e.status = 'PENDING' THEN 2 " +
            "WHEN e.status = 'SHIPPING' THEN 3 " +
            "WHEN e.status = 'SUCCEED' THEN 4 " +
            "WHEN e.status = 'CANCEL' THEN 5 " +
            "ELSE 6 END, " +
            "e.exportDate ASC")
    Page<Export> findAllByWarehouseSorted(@Param("warehouseId") Integer warehouseId, Pageable pageable);

    @Query("SELECT e FROM Export e WHERE e.warehouseFrom.id = :warehouseId")
    Page<Export> findAllByWarehouseWithDefaultSort(@Param("warehouseId") Integer warehouseId, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Export e WHERE e.warehouseFrom.id = :warehouseId " +
            "AND (:status IS NULL OR e.status = :status)")
    int countByWarehouseIdAndStatus(int warehouseId, Status status);
}
