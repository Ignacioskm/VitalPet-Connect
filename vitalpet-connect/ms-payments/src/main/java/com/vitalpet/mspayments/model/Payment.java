package com.vitalpet.mspayments.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    //Relaciones
    @ManyToOne
    @JoinColumn(name = "status_id",nullable = false)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "method_id")
    private PaymentMethod method;

    //Relaciones Lógicas
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "appointement_id", nullable = false)
    private Long appointmentId;
}
