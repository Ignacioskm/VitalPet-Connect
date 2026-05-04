package com.vitalpet.msadoptions.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "adoption")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Adoption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "resolve_date", nullable = false)
    private LocalDateTime resolveDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    //Para referenciar a AdoptionStatus
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private AdoptionStatus adoptionStatus;

    //Para referencias lógicas con microservicios (pet, users y staff)
    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "staff_id", nullable = false)
    private Long staffId;
}
