package com.vitalpet.msclinical.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClinicalResponseDTO {
    private Long id;

    private String reason;
    private String diagnosis;
    private String treatment;
    private String notes;

    private LocalDate visitDate;
    private LocalDateTime createdAt;

    private Long petId;
    private Long staffId;
}
