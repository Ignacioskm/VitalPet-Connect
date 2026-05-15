package com.vitalpet.msappointments.repository;

import com.vitalpet.msappointments.model.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService,Long> {
}
