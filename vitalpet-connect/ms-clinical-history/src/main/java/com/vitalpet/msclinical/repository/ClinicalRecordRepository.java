package com.vitalpet.msclinical.repository;

import com.vitalpet.msclinical.model.ClinicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, Long> {

    //Listar atenciones realizadas por un veterinario en específico
    List<ClinicalRecord> findByStaffId(Long staffId);

    //Obtener el historial de una mascota, de las más reciente a la más antigua.
    List<ClinicalRecord> findByPetIdOrderByVisitDateDesc(Long petId);
}
