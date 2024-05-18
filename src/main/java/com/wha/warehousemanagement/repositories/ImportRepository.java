package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportRepository extends JpaRepository<Import, Integer> {
    Optional<Import> getImportById(int id);
}
