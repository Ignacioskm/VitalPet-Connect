package com.vitalpet.msclinical.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalRecordResponseDTO {
    private Long id;
    private LocalDate visitDate;
    private String reason;
    private String diagnosis;
    private String treatment;
    private String notes;

    private Long petId;
    private Long staffId;

    private List<PrescriptionResponseDTO> prescriptions;
}
