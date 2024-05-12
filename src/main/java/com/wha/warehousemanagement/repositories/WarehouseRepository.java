package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {

    @Query("SELECT new com.wha.warehousemanagement.dtos.WarehouseDTO(w.id, w.name, w.description,w.address,w.createdAt,w.updatedAt) FROM Warehouse w")
    List<WarehouseDTO> getWarehouses();

    @Query("SELECT new com.wha.warehousemanagement.dtos.WarehouseDTO(w.id, w.name, w.description,w.address,w.createdAt,w.updatedAt) FROM Warehouse w WHERE w.id = :id")
    Optional<WarehouseDTO> getWarehouseById(int id);

    @Query("SELECT new com.wha.warehousemanagement.dtos.WarehouseDTO(w.id, w.name, w.description,w.address,w.createdAt,w.updatedAt) " +
            "FROM User u JOIN u.warehouse w WHERE u.id = :userId")
    Optional<WarehouseDTO> getWarehouseByUserId(@Param("userId") Integer userId);

    Optional<WarehouseDTO> findByName(String name);

    Optional<WarehouseDTO> findByAddress(String address);

    Optional<Warehouse> findById(int id);

    boolean existsByName(String name);

    boolean existsByAddress(String address);

    void deleteAll();

}
