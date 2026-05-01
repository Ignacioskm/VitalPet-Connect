package com.vitalpet.msclinical.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalRecordRequestDTO {

    private Long petId;
    private Long staffId;

    private String reason;
    private String diagnosis;
    private String treatment;
    private String notes;
    private List<PrescriptionRequestDTO> prescriptions;
}
