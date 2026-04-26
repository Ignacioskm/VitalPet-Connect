package com.vitalpet.msclinical.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionRequestDTO {
    private String medicationName;
    private String dosage;

    private Integer durationDays;
}
