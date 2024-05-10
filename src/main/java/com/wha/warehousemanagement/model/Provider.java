package com.wha.warehousemanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "providers")
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_id")
    private Integer id;

    @Column(name = "provider_name")
    private String name;

    @Column(name = "provider_email")
    private String email;

    @Column(name = "provider_phone")
    private String phone;

    @Column(name = "provider_address")
    private String address;

    @OneToMany(mappedBy = "provider")
    private Set<Shipment> shipments;
}
