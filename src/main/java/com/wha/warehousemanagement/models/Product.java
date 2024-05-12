package com.wha.warehousemanagement.models;

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
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer id;

    @Column(name = "product_name")
    private String name;

    @Column(name = "product_description")
    private String description;

    @Column(name = "product_quantity")
    private int quantity;

    @Column(name = "product_country")
    private String country;

    @Column(name = "product_received_date")
    private Date receivedDate;

    @OneToMany(mappedBy = "product")
    private Set<ShipmentProduct> shipmentProducts;

    @ManyToOne()
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @OneToMany(mappedBy = "product")
    private Set<OrderDetail> orderDetails;

    public Product(String name, String description, int quantity, String country, Date receivedDate, Category category) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.country = country;
        this.receivedDate = receivedDate;
        this.category = category;
    }
}
