package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.OrderDTO;
import com.wha.warehousemanagement.dtos.responses.OrderResponse;
import com.wha.warehousemanagement.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByCustomerName(String customerName);
    Optional<Order> getOrderById(int id);
    boolean existsByCustomerName(String customerName);
    @Query("SELECT new com.wha.warehousemanagement.dtos.OrderDTO(o.id, o.customerName, o.description, o.quantity, o.status, o.orderDate,o.country) " +
            "FROM Order o WHERE o.id = :id")
    Optional<OrderDTO> getOrderDTOById(@Param("id") int id);

    @Query("SELECT new com.wha.warehousemanagement.dtos.responses.OrderResponse(o.id, o.customerName, o.description, o.quantity, o.status, o.orderDate,o.country) FROM Order o ORDER BY o.id desc")
    List<OrderResponse> getAllOrderResponses();
}
