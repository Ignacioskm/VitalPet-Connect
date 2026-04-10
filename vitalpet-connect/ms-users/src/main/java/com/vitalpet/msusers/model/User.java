package com.vitalpet.msusers.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

// User solo manejará los datos personales, la contraseña y el rol van a ir en el ms-auth.
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(name = "phone_number",nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    //Hibernate crea automaticamente la fecha cuando se crea.
    @CreationTimestamp
    @Column(name = "fecha_creacion",nullable = false)
    private LocalDateTime createdAt;
}
