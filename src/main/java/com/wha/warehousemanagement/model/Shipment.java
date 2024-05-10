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
@Table(name = "shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Integer id;

    @Column(name = "shipment_name")
    private String name;

    @Column(name = "shipment_description")
    private String description;

    @Column(name = "shipment_quantity")
    private int quantity;

    @Column(name = "shipment_status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "shipment_received_date")
    private Date receivedDate;

    @Column(name = "shipment_country")
    private String country;

    @ManyToOne
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @OneToMany(mappedBy = "shipment")
    private Set<ShipmentProduct> shipmentProducts;
}
