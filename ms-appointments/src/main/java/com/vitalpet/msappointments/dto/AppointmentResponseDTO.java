package com.vitalpet.msappointments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {

    private Long id;
    private LocalDateTime scheduledAt;
    private String medicalServiceName;
    private Double price;
    private String notes;
    private LocalDateTime createdAt;

    //Devolvemos el texto (PENDING,CONFIRMED,CANCELLED,COMPLETED)
    private String statusName;

    //Referencias Lógicas
    private Long petId;
    private Long staffId;
    private Long branchId;
}
