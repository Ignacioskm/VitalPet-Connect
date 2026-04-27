package com.vitalpet.msclinical.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="clinical_records")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="clinical_id")
    private Long id;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(nullable = false, length = 500)
    private String reason;

    //ColumnDefinition TEXT hace que hibernate valide el campo pa grandes volumenes de datos
    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    private String notes;

    @CreationTimestamp
    @Column(name = "create_at",updatable = false)
    private LocalDateTime createdAt;

    // Referencias Lógica ms pet
    @Column (name="pet_id", nullable = false)
    private Long petId;

    //Referencia Lógica ms staff
    @Column (name="staff_id", nullable = false)
    private Long staffId;

    //Lo mismo que en el otro ms para que se borren emparejados y en caso de que paso algo haga rollback
    @OneToMany(mappedBy = "clinicalRecord",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions = new ArrayList<>();
}
