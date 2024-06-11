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

    @Column(name = "product_origin")
    private String origin;

    @ManyToOne()
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @OneToMany(mappedBy = "product")
    private Set<ImportDetail> importDetails;

    @OneToMany(mappedBy = "product")
    private Set<Inventory> inventories;

    @OneToMany(mappedBy = "product")
    private Set<ExportDetail> exportDetails;

//    public Product(Integer productId) {
//        this.id = productId;
//    }
}
