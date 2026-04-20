package com.vitalpet.mspets.repository;

import com.vitalpet.mspets.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByActiveTrue();

    List<Pet> findByOwnerId(Long ownerId);

    List<Pet> findBySpeciesName(String speciesName);
}
