package com.wha.warehousemanagement.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wha.warehousemanagement.dtos.ProductOrderDTO;
import com.wha.warehousemanagement.models.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest {
    private String customerName;

    private String description;

    private Integer quantity;

    private Status status;

    private Date orderDate;

    private String country;
}
