package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.OrderDetailDTO;
import com.wha.warehousemanagement.models.OrderDetail;
import com.wha.warehousemanagement.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    Optional<OrderDetail> getOrderDetailById(int id);

    @Query("SELECT new com.wha.warehousemanagement.dtos.OrderDetailDTO(c.id, c.order.id, c.product.id, c.quantity) FROM OrderDetail c WHERE c.id = :id")
    Optional<OrderDetailDTO> getOrderDetailDTOById(int id);
}
