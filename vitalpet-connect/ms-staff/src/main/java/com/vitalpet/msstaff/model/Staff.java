package com.vitalpet.msstaff.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column (nullable = false)
    private String speciality;

    private Boolean active = true;

    private LocalDate hireDate;

    //Referencia logica branch Martina.
    @Column (name="branch_id", nullable = false)
    private Long branchId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StaffSchedule> schedules = new ArrayList<>();
}
