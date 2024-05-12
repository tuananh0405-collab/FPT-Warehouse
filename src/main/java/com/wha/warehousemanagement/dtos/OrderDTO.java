package com.wha.warehousemanagement.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class OrderDTO {

        private Integer id;

        private String customerName;

        private String description;

        private int quantity;

        private Status status;

        private Date orderDate;

        private String country;

        private List<ProductOrderDTO> products;

        public OrderDTO(Integer id, String customerName, String description, int quantity, Status status, Date orderDate, String country) {
            this.id = id;
            this.customerName = customerName;
            this.description = description;
            this.quantity = quantity;
            this.status = status;
            this.orderDate = orderDate;
            this.country = country;
        }

}

