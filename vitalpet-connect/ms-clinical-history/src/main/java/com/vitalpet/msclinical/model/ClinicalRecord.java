package com.vitalpet.msclinical.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="clinical_records")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="clinical_id")
    private Long id;

    @Column(nullable = false)
    private String reason;

    private String diagnosis;

    private String notes;

    private String treatment;

    @Column(name="visit_date", nullable = false)
    private LocalDate visitDate;

    @CreationTimestamp
    @Column(name = "create_at",updatable = false)
    private LocalDateTime createdAt;

    // Referencias Lógicas.
    @Column (name="pet_id", nullable = false)
    private Long petId;

    @Column (name="staff_id", nullable = false)
    private Long staffId;
}
