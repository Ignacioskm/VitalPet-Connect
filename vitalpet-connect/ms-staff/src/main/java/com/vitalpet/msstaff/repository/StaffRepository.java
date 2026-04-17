package com.vitalpet.msstaff.repository;

import com.vitalpet.msstaff.model.Specialty;
import com.vitalpet.msstaff.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff,Long> {
    Optional<Staff> findByEmail(String email);
    List<Staff> findBySpecialtyName(String name);
    List<Staff> findByActiveTrue();
    boolean existsByEmail(String email);
    // Implementar con webClient List<Staff> findByBranchId();
}
