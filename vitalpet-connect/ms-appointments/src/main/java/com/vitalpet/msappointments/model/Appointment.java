package com.vitalpet.msappointments.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    //Referencia a AppointmentStatus
    @ManyToOne
    @JoinColumn(name = "status_id",nullable = false)
    private AppointmentStatus appointmentStatus;


    //Referencias Lógicas
    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;
}
