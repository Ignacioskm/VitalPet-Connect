package com.vitalpet.msclinical.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalRequestDTO {

    private String diagnosis;
    private String reason;
    private String treatment;
    private String notes;

    private LocalDate visitDate;

    private Long petId;
    private Long staffId;
}
