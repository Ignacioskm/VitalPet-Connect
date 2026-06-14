package com.vitalpet.msadoptions.service;

import com.vitalpet.msadoptions.client.PetClient;
import com.vitalpet.msadoptions.client.UserClient;
import com.vitalpet.msadoptions.dto.AdoptionRequestDTO;
import com.vitalpet.msadoptions.dto.AdoptionResponseDTO;
import com.vitalpet.msadoptions.dto.PetResponseDTO;
import com.vitalpet.msadoptions.exception.ResourceNotFoundException;
import com.vitalpet.msadoptions.model.Adoption;
import com.vitalpet.msadoptions.model.AdoptionStatus;
import com.vitalpet.msadoptions.repository.AdoptionRepository;
import com.vitalpet.msadoptions.repository.AdoptionStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdoptionService {

    @Autowired  private AdoptionRepository adoptionRepository;
    @Autowired  private AdoptionStatusRepository adoptionStatusRepository;
    @Autowired  private PetClient petClient;
    @Autowired  private UserClient userClient;


    public AdoptionResponseDTO create(AdoptionRequestDTO dto) {

        //Validamos que la mascota exista y esté disponible pa adoptar
        PetResponseDTO pet;
        try {
            pet = petClient.getPetById(dto.getPetId());
        } catch (Exception e){
            throw new ResourceNotFoundException("La mascota con ID "+ dto.getPetId());
        }

        if(pet.getOwnerId() != null){
            throw new IllegalArgumentException("Error: La mascota " + pet.getName() + " ya tiene un dueño asignado");
        }

        //Validamos usuario y rol , que exista y que sea client
        if (!userClient.existById(dto.getUserId())) {
            throw new ResourceNotFoundException("El usuario con ID " + dto.getUserId() + " no existe.");
        }

        if (Boolean.FALSE.equals(userClient.isClient(dto.getUserId()))) {
            throw new IllegalArgumentException("Error: Solo los usuarios registrados como CLIENT pueden realizar solicitudes de adopción.");
        }

        //Se asigna estado pending
        AdoptionStatus adoptionStatus = adoptionStatusRepository.findByName("PENDING")
                .orElseThrow(() -> new ResourceNotFoundException("Estado PENDING no encontrado."));

        Adoption adoption = new Adoption();
        adoption.setNotes(dto.getNotes());
        adoption.setAdoptionStatus(adoptionStatus); //Se setea ya que por defecto el estado es PENDING
        adoption.setPetId(dto.getPetId());
        adoption.setUserId(dto.getUserId());
        adoption.setRequestDate(LocalDateTime.now());

        return toDTO(adoptionRepository.save(adoption));
    }

    //Trae todas las adopciones
    public List<AdoptionResponseDTO> getAll() {
        return adoptionRepository.findAll().stream().map(this::toDTO).toList();
    }

    //Trae mascotas disponibles para adopción
    public List<PetResponseDTO> getAvailablePets() {
        return  petClient.getAvailablePets();
    }

    //Aprobar adopción
    public AdoptionResponseDTO approve(Long id, Long staffId){
        Adoption adoption = adoptionRepository.findById(id).orElseThrow((() -> new ResourceNotFoundException("Adopción no encontrada")));

        AdoptionStatus approved = adoptionStatusRepository.findByName("APPROVED").orElseThrow(() -> new ResourceNotFoundException("Estado APPROVED no existe"));

        adoption.setAdoptionStatus(approved);
        adoption.setStaffId(staffId);
        adoption.setResolveDate(LocalDateTime.now());

        Adoption saved = adoptionRepository.save(adoption);

        petClient.updateOwner(adoption.getPetId(),adoption.getUserId());

        return toDTO(saved);

    }

    //Rechazar
    public AdoptionResponseDTO reject(Long id, Long staffId){

        Adoption adoption = adoptionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Adopción no encontrada"));

        AdoptionStatus rejected = adoptionStatusRepository.findByName("REJECTED").orElseThrow(() -> new ResourceNotFoundException("Estado REJECTED no existe"));

        adoption.setAdoptionStatus(rejected);
        adoption.setStaffId(staffId);
        adoption.setResolveDate(LocalDateTime.now());

        return toDTO(adoptionRepository.save(adoption));
    }




    public AdoptionResponseDTO toDTO(Adoption adop) {
        AdoptionResponseDTO dto = new AdoptionResponseDTO();
        dto.setId(adop.getId());
        dto.setRequestDate(adop.getRequestDate());
        dto.setResolveDate(adop.getResolveDate());
        dto.setNotes(adop.getNotes());
        dto.setCreatedAt(adop.getCreatedAt());
        dto.setPetId(adop.getPetId());
        dto.setUserId(adop.getUserId());
        dto.setStaffId(adop.getStaffId());

        if (adop.getAdoptionStatus() != null) {
            dto.setStatusName(adop.getAdoptionStatus().getName()); //Agrega el nombre del status para mostrarlo en JSON
        }
        return dto;
    }

}
