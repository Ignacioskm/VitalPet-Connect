package com.vitalpet.msclinical.dto;

import lombok.Data;

@Data
public class PrescriptionResponseDTO {
    private Long id;

    private String medicationName;
    private String dosage;
    private Integer durationDays;

    private Long clinicalId;
}
