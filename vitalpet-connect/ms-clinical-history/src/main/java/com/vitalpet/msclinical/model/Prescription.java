package com.vitalpet.msclinical.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    @Column(nullable = false)
    private String dosage;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    //FetchType LAZY nos sirve para evitar cargar datos que no necesitemos, EAGER que es el default
    //Lazy = "Trae solo lo que te pida", EAGER = "Trae altiro"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name="clinical_id", nullable = false)
    private ClinicalRecord clinicalRecord;
}
