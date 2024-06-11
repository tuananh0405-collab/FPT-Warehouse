package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.ExportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportDetailRepository extends JpaRepository<ExportDetail, Integer> {
    List<ExportDetail> findByExportId(Integer exportId);
}
