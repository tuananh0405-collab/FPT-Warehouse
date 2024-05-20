package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.responses.ExportResponse;
import com.wha.warehousemanagement.models.Export;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportRepository extends JpaRepository<Export, Integer> {
}
