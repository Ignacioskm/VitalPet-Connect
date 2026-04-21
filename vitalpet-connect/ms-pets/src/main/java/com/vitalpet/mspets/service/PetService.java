package com.vitalpet.mspets.service;

import com.vitalpet.mspets.dto.PetRequestDTO;
import com.vitalpet.mspets.dto.PetResponseDTO;
import com.vitalpet.mspets.model.Pet;
import com.vitalpet.mspets.model.Species;
import com.vitalpet.mspets.client.UserClient;
import com.vitalpet.mspets.repository.PetRepository;
import com.vitalpet.mspets.repository.SpeciesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private SpeciesRepository speciesRepository;

    @Autowired
    private UserClient userOwner;

    private PetResponseDTO toDTO(Pet pet) {
        PetResponseDTO dto = new PetResponseDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setBreed(pet.getBreed());
        dto.setBirthDate(pet.getBirthDate());
        dto.setWeight(pet.getWeight());
        dto.setActive(pet.getActive());
        dto.setCreatedAt(pet.getCreatedAt());
        dto.setSpeciesId(pet.getSpecies().getId());
        return dto;
    }

    public List<PetResponseDTO> getAll() {
        return petRepository.findByActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PetResponseDTO getById(Long id) {
        Pet pet = petRepository.findById(id).orElseThrow(() -> new RuntimeException("Mascota no encontrada."));
        return toDTO(pet);
    }

    public PetResponseDTO create(PetRequestDTO dto) {
        Boolean userExist = userOwner.existById(dto.getOwnerId());

        if (Boolean.FALSE.equals(userExist)) {
            throw new RuntimeException("Error: El dueño con ID " + dto.getOwnerId() + " no existe.");
        }

        Species species = speciesRepository.findById(dto.getSpeciesId()).orElseThrow(() -> new RuntimeException("Error: La especie con ID " + dto.getSpeciesId() + " no existe."));

        //falta aquí buscar por rol CLIENTE de user



        Pet pet = new Pet();
        pet.setName(dto.getName());
        pet.setBreed(dto.getBreed());
        pet.setBirthDate(dto.getBirthDate());
        pet.setWeight(dto.getWeight());
        pet.setActive(true);
        pet.setOwnerId(dto.getOwnerId());
        pet.setSpecies(species);

        return toDTO(petRepository.save(pet));
    }

    public PetResponseDTO update(Long id, PetRequestDTO dto) {
        Pet existing = petRepository.findById(id).orElseThrow(() -> new RuntimeException("Error: La mascota con ID " + id + " no existe."));

        Species species = speciesRepository.findById(dto.getSpeciesId()).orElseThrow(() -> new RuntimeException("Error: La especie con ID " + dto.getSpeciesId() + " no existe."));

        Boolean userExist = userOwner.existById(dto.getOwnerId());

        if (Boolean.FALSE.equals(userExist)) {
            throw new RuntimeException("Error: El dueño con ID " + dto.getOwnerId() + " no existe.");
        }

        existing.setName(dto.getName());
        existing.setBreed(dto.getBreed());
        existing.setBirthDate(dto.getBirthDate());
        existing.setWeight(dto.getWeight());
        existing.setSpecies(species);
        existing.setOwnerId(dto.getOwnerId());

        return toDTO(petRepository.save(existing));

    }

    public void deactivate(Long id) {
        Pet pet = petRepository.findById(id).orElseThrow(() -> new RuntimeException("Mascota no encontrada."));

        pet.setActive(false);
        petRepository.save(pet);
    }

    public  List<PetResponseDTO> getByOwnerId(Long ownerId) {
        return petRepository.findByOwnerId(ownerId).stream().map(this::toDTO).collect(Collectors.toList());
    }


    public List<PetResponseDTO> getBySpeciesName(String speciesName) {
        return petRepository.findBySpeciesName(speciesName).stream().map(this::toDTO).collect(Collectors.toList());
    }
}
