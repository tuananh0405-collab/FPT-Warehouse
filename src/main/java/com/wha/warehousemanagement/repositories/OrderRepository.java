package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.OrderDTO;
import com.wha.warehousemanagement.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByCustomerName(String customerName);
    Optional<Order> getOrderById(int id);

    @Query("SELECT new com.wha.warehousemanagement.dtos.OrderDTO(c.id, c.customerName, c.description, c.quantity, c.status, c.orderDate, c.country) FROM Order c WHERE c.id = :id")
    Optional<OrderDTO> getOrderDTOById(int id);

}
