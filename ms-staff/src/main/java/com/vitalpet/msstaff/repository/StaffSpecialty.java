package com.vitalpet.msstaff.repository;


import com.vitalpet.msstaff.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffSpecialty extends JpaRepository<Specialty,Long> {
    Optional<Specialty> findByName(String name);
}
