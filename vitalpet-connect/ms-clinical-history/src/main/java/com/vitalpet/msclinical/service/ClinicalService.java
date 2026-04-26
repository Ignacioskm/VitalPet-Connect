package com.vitalpet.msclinical.service;

import com.vitalpet.msclinical.dto.ClinicalRequestDTO;
import com.vitalpet.msclinical.dto.ClinicalResponseDTO;
import com.vitalpet.msclinical.model.ClinicalRecord;
import com.vitalpet.msclinical.repository.ClinicalRecordRepository;
import com.vitalpet.msclinical.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClinicalService {
    @Autowired
    private ClinicalRecordRepository clinicalRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    private ClinicalResponseDTO toDTO(ClinicalRecord clinicalRecord){
        ClinicalResponseDTO dto = new ClinicalResponseDTO();

        dto.setId(clinicalRecord.getId());
        dto.setDiagnosis(clinicalRecord.getDiagnosis());
        dto.setReason(clinicalRecord.getReason());
        dto.setNotes(clinicalRecord.getNotes());
        dto.setCreatedAt(clinicalRecord.getCreatedAt());
        dto.setPetId(clinicalRecord.getPetId());
        dto.setStaffId(clinicalRecord.getStaffId());

        return dto;
    }

    public ClinicalResponseDTO getById(Long id){
        ClinicalRecord clinicalRecord = clinicalRepository.findById(id).orElseThrow(() -> new RuntimeException("Expediente no encontrado"));
        return toDTO(clinicalRecord);
    }

    public ClinicalResponseDTO create(ClinicalRequestDTO dto){
        if(clinicalRepository.existsByPetId(dto.getPetId()) || clinicalRepository.existsByStaffId(dto.getStaffId())){
            throw new RuntimeException("La mascota ya está registrada");
        }
        ClinicalRecord clinicalRecord = new ClinicalRecord();

        clinicalRecord.setDiagnosis(dto.getDiagnosis());
        clinicalRecord.setReason(dto.getReason());
        clinicalRecord.setTreatment(dto.getTreatment());
        clinicalRecord.setNotes(dto.getNotes());
        clinicalRecord.setVisitDate(dto.getVisitDate());
        clinicalRecord.setPetId(dto.getPetId());
        clinicalRecord.setStaffId(dto.getStaffId());

        return toDTO(clinicalRepository.save(clinicalRecord));
    }


}
