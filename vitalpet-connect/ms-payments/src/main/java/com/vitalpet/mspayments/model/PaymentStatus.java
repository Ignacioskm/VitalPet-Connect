package com.vitalpet.mspayments.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_status")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PENDING , PAID,  REFUNDED
    @Column(nullable = false,unique = true)
    private String name;
}
