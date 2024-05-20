package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.ExportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportDetailRepository extends JpaRepository<ExportDetail, Integer> {
}
