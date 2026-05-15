package com.vitalpet.msadoptions.repository;

import com.vitalpet.msadoptions.model.AdoptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdoptionStatusRepository extends JpaRepository<AdoptionStatus, Long> {

    Optional<AdoptionStatus> findByName(String name);
}
