package com.wha.warehousemanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer id;

    @Column(name = "order_customer_name")
    private String customerName;

    @Column(name = "order_description")
    private String description;

    @Column(name = "order_quantity")
    private int quantity;

    @Column(name = "order_status")
    private Status status;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "order_country")
    private String country;

    @OneToMany(mappedBy = "order")
    private Set<OrderDetail> orderDetails;

}
