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
@Table(name = "exports")
public class Export {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "export_id")
    private Integer id;

    @Column(name = "export_customer_name")
    private String customerName;

    @Column(name = "export_description")
    private String description;

    @Column(name = "export_status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "export_date")
    private Date exportDate;

    @Column(name = "export_customer_address")
    private String customerAddress;

    @OneToMany(mappedBy = "export")
    private Set<ExportDetail> exportDetails;

}
