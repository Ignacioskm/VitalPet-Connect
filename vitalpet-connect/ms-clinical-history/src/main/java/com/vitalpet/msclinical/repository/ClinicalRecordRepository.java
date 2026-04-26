package com.vitalpet.msclinical.repository;

import com.vitalpet.msclinical.model.ClinicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, Long> {
    List<ClinicalRecord> findByStaffId(Long staffId);
    List<ClinicalRecord> findByPetIdOrderByVisitDateDesc(Long petId);
    boolean existsByPetId(Long petId);
    boolean existsByStaffId(Long staffId);
}
