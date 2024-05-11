package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {

    Optional<WarehouseDTO> findByName(String name);
    Optional<WarehouseDTO> findByAddress(String address);

    boolean existsByName(String name);

    boolean existsByAddress(String address);

    void deleteAll();

}
