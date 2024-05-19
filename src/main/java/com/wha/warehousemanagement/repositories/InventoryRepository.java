package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    @Query("SELECT i FROM Inventory i " +
            "JOIN i.product p " +
            "JOIN p.category c " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId AND (:categoryId IS NULL OR c.id = :categoryId)")
    Page<Inventory> findByWarehouseIdAndCategoryId(Integer warehouseId, Integer categoryId, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Inventory i JOIN i.zone z JOIN z.warehouse w WHERE w.id = :warehouseId")
    Long countInventoriesByWarehouseId(Integer warehouseId);
}
