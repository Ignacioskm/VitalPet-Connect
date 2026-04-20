package com.vitalpet.mspets.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "pets")
@AllArgsConstructor
@NoArgsConstructor
public class Pet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_name",nullable = false)
    private String name;

    @Column
    private String breed;

    @Column(name = "birth_date",nullable = false)
    private LocalDate birthDate;

    @Column
    private double weight;

    @Column(nullable = false)
    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    //Relación con Species
    @ManyToOne
    @Column(name = "species_name",nullable = false)
    private Species species;

    //Relación con User mediante owner_id
    @Column(name = "owner_id",nullable = false)
    private Long ownerId;
}
