package com.vitalpet.msappointments.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_services")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name; // Vacunación, consulta general, emergencia etc.

    @Column(nullable = false)
    private Double price;
}
