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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription")
    private Long id;

    @Column(nullable = false)
    private String medicationName;

    @Column(nullable = false)
    private String dosage;

    private Integer durationDays;

    // Referencia Lógica
    @ManyToOne
    @JoinColumn (name="clinical_id", nullable = false)
    private ClinicalRecord clinicalRecord;
}
