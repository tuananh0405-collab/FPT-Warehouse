package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    @Query("SELECT i FROM Inventory i " +
            "JOIN i.product p " +
            "JOIN p.category c " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:zoneName IS NULL OR z.name = :zoneName)")
    Page<Inventory> findByWarehouseIdAndCategoryId(Integer warehouseId, Integer categoryId, String zoneName, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Inventory i " +
            "JOIN i.product p " +
            "JOIN p.category c " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:zoneName IS NULL OR z.name = :zoneName)")
    Long countInventoriesByWarehouseIdAndCategoryId(Integer warehouseId, Integer categoryId, String zoneName);

    @Query("SELECT COUNT(i) FROM Inventory i JOIN i.zone z JOIN z.warehouse w WHERE w.id = :warehouseId")
    Long countInventoriesByWarehouseId(Integer warehouseId);


    // zones transfer
    Inventory findByProductIdAndZoneId(int productId, int zoneId);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.zone.warehouse.id = :warehouseId ORDER BY i.expiredAt ASC")
    List<Inventory> findByProductIdAndWarehouseIdOrderByExpiredAtAsc(@Param("productId") Integer productId, @Param("warehouseId") Integer warehouseId);

    @Query("SELECT SUM(i.quantity) FROM Inventory i WHERE i.product.id = :productId AND i.zone.warehouse.id = :warehouseId")
    Integer countTotalQuantityByProductIdAndWarehouseId(@Param("productId") Integer productId, @Param("warehouseId") Integer warehouseId);

    @Query("SELECT i FROM Inventory i " +
            "JOIN i.product p " +
            "JOIN p.category c " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:zoneName IS NULL OR z.name = :zoneName) " +
            "AND (:productName IS NULL OR p.name LIKE %:productName%) " +
            "AND (:quantityLow IS NULL OR i.quantity >= :quantityLow) " +
            "AND (:quantityHigh IS NULL OR i.quantity <= :quantityHigh)")
    Page<Inventory> searchInventoriesWithFilters(
            Integer warehouseId,
            Integer categoryId,
            String zoneName,
            String productName,
            Integer quantityLow,
            Integer quantityHigh,
            Pageable pageable);

    @Query("SELECT COUNT(i) FROM Inventory i " +
            "JOIN i.product p " +
            "JOIN p.category c " +
            "JOIN i.zone z " +
            "JOIN z.warehouse w " +
            "WHERE w.id = :warehouseId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:zoneName IS NULL OR z.name = :zoneName) " +
            "AND (:productName IS NULL OR p.name LIKE %:productName%) " +
            "AND (:quantityLow IS NULL OR i.quantity >= :quantityLow) " +
            "AND (:quantityHigh IS NULL OR i.quantity <= :quantityHigh)")
    Long countInventoriesWithFilters(
            Integer warehouseId,
            Integer categoryId,
            String zoneName,
            String productName,
            Integer quantityLow,
            Integer quantityHigh);
}
