package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Import;
import com.wha.warehousemanagement.models.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportRepository extends JpaRepository<Import, Integer> {
    Optional<Import> getImportById(int id);

    @Query("SELECT i FROM Import i")
    Page<Import> findAllImports(Pageable pageable);

    @Query("SELECT COUNT(i) FROM Import i")
    Long countAllImports();

}
