package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.ProductDTO;
import com.wha.warehousemanagement.dtos.ProductOrderDTO;
import com.wha.warehousemanagement.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);
    Optional<Product> getProductById(int id);

    @Query("SELECT new com.wha.warehousemanagement.dtos.ProductDTO(c.id, c.name, c.description, c.quantity, c.country, c.receivedDate, c.category.id) FROM Product c WHERE c.id = :id")
    Optional<ProductDTO> getProductDTOById(int id);

    @Query("SELECT new com.wha.warehousemanagement.dtos.ProductOrderDTO(p.id, p.name, p.description, d.quantity) " +
            "FROM OrderDetail d JOIN d.product p WHERE d.order.id = :orderId")
    List<ProductOrderDTO> findProductOrderByOrderId(@Param("orderId") int orderId);
}
