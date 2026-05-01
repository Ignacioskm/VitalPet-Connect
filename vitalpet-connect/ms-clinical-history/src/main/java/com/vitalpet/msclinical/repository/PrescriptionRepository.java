package com.vitalpet.msclinical.repository;

import com.vitalpet.msclinical.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByClinicalRecordId(Long Id);
    List<Prescription> findByMedicationName(String medicationName);
}
