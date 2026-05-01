package com.vitalpet.msstaff.repository;

import com.vitalpet.msstaff.model.Staff;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff,Long> {
    Optional<Staff> findByEmail(String email);
    List<Staff> findBySpecialtyName(String name);
    List<Staff> findByActiveTrue();
    boolean existsByEmail(String email);
    //Acá traemos a los staff por branch id si aparte quisieramos traer los activos le mandamos un AndActiveTrue(Long branchId)
    //Y JPA haría la consulta SELECT * FROM staff WHERE branch_id = ? AND active = true; algo así
    List<Staff> findByBranchId(Long branchId);
    //Verificar si existe por id para clinical history
    boolean existsById(@NonNull Long id);
}
