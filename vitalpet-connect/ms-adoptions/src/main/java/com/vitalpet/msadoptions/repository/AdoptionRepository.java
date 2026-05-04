package com.vitalpet.msadoptions.repository;

import com.vitalpet.msadoptions.model.Adoption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {

    List<Adoption> findByActiveTrue();

    List<Adoption> findByUserId(Long userId);

    List<Adoption> findByStaffId(Long staffId);

    boolean findByPetId(Long petId);

}
