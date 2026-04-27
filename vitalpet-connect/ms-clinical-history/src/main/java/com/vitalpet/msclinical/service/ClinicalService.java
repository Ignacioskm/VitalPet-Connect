package com.vitalpet.msclinical.service;

import com.vitalpet.msclinical.client.PetClient;
import com.vitalpet.msclinical.client.StaffClient;
import com.vitalpet.msclinical.dto.ClinicalRecordRequestDTO;
import com.vitalpet.msclinical.dto.ClinicalRecordResponseDTO;
import com.vitalpet.msclinical.dto.PrescriptionRequestDTO;
import com.vitalpet.msclinical.dto.PrescriptionResponseDTO;
import com.vitalpet.msclinical.model.ClinicalRecord;
import com.vitalpet.msclinical.model.Prescription;
import com.vitalpet.msclinical.repository.ClinicalRecordRepository;
import com.vitalpet.msclinical.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClinicalService {
    @Autowired
    private ClinicalRecordRepository clinicalRepository;

    //OpenFeign
    @Autowired
    private PetClient petClient;

    @Autowired
    private StaffClient staffClient;



    public ClinicalRecordResponseDTO create(ClinicalRecordRequestDTO dto){
       //Validamos que pet y staff existan
       Boolean petExists = petClient.existsById(dto.getPetId());
       Boolean staffExists = staffClient.existsById(dto.getStaffId());

       if(!petExists){
           throw new RuntimeException("Error: La mascota con ID " + dto.getPetId() + " no existe");
       }

       if(!staffExists){
           throw new RuntimeException("Error: El funcionario con ID " + dto.getStaffId() + " no existe");
       }

        //Convertimos el DTO principal a entidad.
        ClinicalRecord clinicalRecord = toClinicalRecordEntity(dto);

        //Ahora validamos que las prescripciones existan y que no vengan vacías
       if(dto.getPrescriptions() != null && !dto.getPrescriptions().isEmpty()){
           //Acá hacemos lo mismo que en staff, pasamos de dto a entidad y lo guardamos en una lista.
           List<Prescription> prescriptions = dto.getPrescriptions().stream().map(pDto -> toPrescriptionEntity(pDto,clinicalRecord))
                   .toList();

           clinicalRecord.setPrescriptions(prescriptions);
       }

       //Guardamos en la BDD y devolvemos el DTO limpio
        ClinicalRecord savedRecord = clinicalRepository.save(clinicalRecord);
        return toDTO(savedRecord);
    }

    //Según la lógica de negocio buscamos historial por mascota
    public List<ClinicalRecordResponseDTO> getHistoryByPet(Long petId){
        return clinicalRepository.findByPetIdOrderByVisitDateDesc(petId).stream().map(this::toDTO).collect(Collectors.toList());
    }




    //Prescription de DTO a Entity
    private Prescription toPrescriptionEntity(PrescriptionRequestDTO dto, ClinicalRecord clinicalRecord){
        Prescription p = new Prescription();
        p.setMedicationName(dto.getMedicationName());
        p.setDosage(dto.getDosage());
        p.setDurationDays(dto.getDurationDays());

        //Aca hacemos el vínculo bidireccional
        p.setClinicalRecord(clinicalRecord);
        return p;
    }

    //Clinical record de DTO a Entity
    private ClinicalRecord toClinicalRecordEntity(ClinicalRecordRequestDTO dto){
        ClinicalRecord clinicalRecord = new ClinicalRecord();
        clinicalRecord.setPetId(dto.getPetId());
        clinicalRecord.setStaffId(dto.getStaffId());
        //usamos local now pormientras
        clinicalRecord.setVisitDate(LocalDate.now());
        clinicalRecord.setReason(dto.getReason());
        clinicalRecord.setDiagnosis(dto.getDiagnosis());
        clinicalRecord.setTreatment(dto.getTreatment());
        clinicalRecord.setNotes(dto.getNotes());
        return clinicalRecord;
    }

    //Clinical Record entity a  DTO
    private ClinicalRecordResponseDTO toDTO(ClinicalRecord clinicalRecord){
        ClinicalRecordResponseDTO dto = new ClinicalRecordResponseDTO();

        dto.setId(clinicalRecord.getId());
        dto.setVisitDate(clinicalRecord.getVisitDate());
        dto.setReason(clinicalRecord.getReason());
        dto.setDiagnosis(clinicalRecord.getDiagnosis());
        dto.setTreatment(clinicalRecord.getTreatment());
        dto.setNotes(clinicalRecord.getNotes());

        dto.setPetId(clinicalRecord.getPetId());
        dto.setStaffId(clinicalRecord.getStaffId());

        //Como queremos agregar en cascadas las prescripciones osea que vengan dentro de la request de clinical record
        //Ahora hacemos la lógica del toDTO de prescripciones pero aquí dentro del toDTO de clinicalRecord

        //Validamos que la lista de prescriptions sea distinta a null
        //Aca básicamente ocupamos el toDTO de prescription, primero creamos una lista de sus DTO y lo mapeamos con programación funcional
        //La explicación de esto está en el notion o en staff.
        if(clinicalRecord.getPrescriptions() != null){
            List<PrescriptionResponseDTO> prescriptionResponseDTOS = clinicalRecord.getPrescriptions().stream()
                    .map(this::toPrescriptionDTO)
                    .toList();
            dto.setPrescriptions(prescriptionResponseDTOS);
        }

        return dto;
    }

    //Prescription entity a DTO
    private PrescriptionResponseDTO toPrescriptionDTO(Prescription prescription){

        PrescriptionResponseDTO  pDTO = new PrescriptionResponseDTO();

        pDTO.setId(prescription.getId());
        pDTO.setMedicationName(prescription.getMedicationName());
        pDTO.setDosage(prescription.getDosage());
        pDTO.setDurationDays(prescription.getDurationDays());

        return pDTO;
    }

}
