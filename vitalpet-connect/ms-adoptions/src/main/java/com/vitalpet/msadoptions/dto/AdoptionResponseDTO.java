package com.vitalpet.msadoptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdoptionResponseDTO {

    private Long id;
    private LocalDateTime requestDate;
    private LocalDateTime resolveDate;
    private String notes;
    private LocalDateTime createdAt;

    //Devolver PENDING, APPROVED, REJECTED, COMPLETED del AdoptionStatus
    private String statusName;

    //Referencias lógicas
    private Long petId;
    private Long userId;
    private Long staffId;
}
