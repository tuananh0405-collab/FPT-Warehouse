package com.wha.warehousemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderDTO {
    private Integer productId;
    private String productName;
    private String productDescription;
    private int orderQuantity;
}
