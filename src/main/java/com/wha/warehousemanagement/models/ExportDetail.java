package com.wha.warehousemanagement.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "export_details")
public class ExportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "export_detail_id")
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "export_id", nullable = true)
    private Export export;

    @ManyToOne()
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @Column(name = "export_detail_quantity")
    private Integer quantity;

    @ManyToOne()
    @JoinColumn(name = "zone_id", nullable = true)
    private Zone zone;
}
