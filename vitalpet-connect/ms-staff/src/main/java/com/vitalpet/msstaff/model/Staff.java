package com.vitalpet.msstaff.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "staff")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Staff {
    @Id
    @Column(name = "staff_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name="first_name", nullable = false)
    private String firstName;

    @Column (name="last_name")
    private String lastName;

    @Column (nullable = false,unique = true)
    private String email;

    @Column (name="phone_number", nullable = false)
    private String phoneNumber;

    private Boolean active = true;

    private LocalDate hireDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    //Referencia logica branch Martina.
    @Column (name="branch_id", nullable = false)
    private Long branchId;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StaffSchedule> schedules = new ArrayList<>();

    @ManyToOne
    private Specialty specialty;
}
