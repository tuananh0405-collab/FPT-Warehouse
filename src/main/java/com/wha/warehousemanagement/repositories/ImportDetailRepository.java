package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.responses.ImportDetailResponse;
import com.wha.warehousemanagement.models.ImportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportDetailRepository extends JpaRepository<ImportDetail, Integer> {
    @Query("SELECT new com.wha.warehousemanagement.dtos.responses.ImportDetailResponse(d.id, " +
            "new com.wha.warehousemanagement.dtos.responses.ProductResponse(p.name, p.description, p.origin, " +
            "new com.wha.warehousemanagement.dtos.responses.CategoryResponse(c.name)), " +
            "d.quantity, d.expiredAt, z.name) " +
            "FROM ImportDetail d " +
            "JOIN d.product p " +
            "JOIN p.category c " +
            "JOIN d.zone z " +
            "WHERE d.anImport.id = :importId")
    List<ImportDetailResponse> findImportDetailResponsesByImportId(@Param("importId") Integer importId);
}
