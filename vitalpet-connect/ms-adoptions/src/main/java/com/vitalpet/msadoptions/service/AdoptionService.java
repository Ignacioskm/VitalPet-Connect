package com.vitalpet.msadoptions.service;

import com.vitalpet.msadoptions.client.PetClient;
import com.vitalpet.msadoptions.client.StaffClient;
import com.vitalpet.msadoptions.client.UserClient;
import com.vitalpet.msadoptions.dto.AdoptionRequestDTO;
import com.vitalpet.msadoptions.dto.AdoptionResponseDTO;
import com.vitalpet.msadoptions.model.Adoption;
import com.vitalpet.msadoptions.model.AdoptionStatus;
import com.vitalpet.msadoptions.repository.AdoptionRepository;
import com.vitalpet.msadoptions.repository.AdoptionStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdoptionService {

    @Autowired
    private AdoptionRepository adoptionRepository;

    @Autowired
    private AdoptionStatusRepository adoptionStatusRepository;

    @Autowired
    private PetClient petClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private StaffClient staffClient;

    public AdoptionResponseDTO toDTO(Adoption adop) {
        AdoptionResponseDTO dto = new AdoptionResponseDTO();
        dto.setId(adop.getId());
        dto.setRequestDate(adop.getRequestDate());
        dto.setResolveDate(adop.getResolveDate());
        dto.setNotes(adop.getNotes());
        dto.setPetId(adop.getPetId());
        dto.setUserId(adop.getUserId());
        dto.setStaffId(adop.getStaffId());

        if (adop.getAdoptionStatus() != null) {
            dto.setStatusName(adop.getAdoptionStatus().getName()); //Agrega el nombre del status para mostrarlo en JSON
        }

        return dto;
    }

    public AdoptionResponseDTO create(AdoptionRequestDTO dto) {

        if (petClient.)

        if (!petClient.existById(dto.getPetId())) throw new RuntimeException("La mascota no existe.");
        if (!userClient.existById(dto.getUserId())) throw new RuntimeException("El usuario no existe.");
        if (!userClient.isClient(dto.getUserId())) throw new RuntimeException("El usuario no es CLIENT.");
        if (!staffClient.existById(dto.getStaffId())) throw new RuntimeException("El Staff no existe.");

        AdoptionStatus adoptionStatus = adoptionStatusRepository.findByName("PENDING").orElseThrow(() -> new RuntimeException("Estado PENDING no encontrado."));

        Adoption adoption = new Adoption();
        adoption.setNotes(dto.getNotes());
        adoption.setAdoptionStatus(adoptionStatus); //Se setea ya que por defecto el estado es PENDING
        adoption.setPetId(dto.getPetId());
        adoption.setUserId(dto.getUserId());
        adoption.setStaffId(dto.getStaffId());

        return toDTO(adoptionRepository.save(adoption));
    }

    //Trae todas las adopciones
    public List<AdoptionResponseDTO> getAll() {
        return adoptionRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    //Trae adopciones por user
    public List<AdoptionResponseDTO> getByUser(Long userId) {
        return adoptionRepository.findByUserId(userId).stream().map(this::toDTO).toList();
    }

    //Trae mascotas disponibles para adopción
    public List<Object> getAvailablePets() {
        return  petClient.getAvailablePets();
    }
}
