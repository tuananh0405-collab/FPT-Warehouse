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
@Table(name = "imports")
public class Import {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "import_id")
    private Integer id;

    @Column(name = "import_description")
    private String description;

    @Column(name = "import_status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "import_received_date")
    private Date receivedDate;

    @ManyToOne()
    @JoinColumn(name = "provider_id", nullable = true)
    private Provider provider;

    @OneToMany(mappedBy = "anImport")
    private Set<ImportDetail> importDetails;
}
