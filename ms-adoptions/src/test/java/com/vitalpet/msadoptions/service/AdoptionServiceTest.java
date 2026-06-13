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
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionServiceTest {

    @Mock private AdoptionRepository adoptionRepository;
    @Mock private AdoptionStatusRepository adoptionStatusRepository;
    @Mock private PetClient petClient;
    @Mock private UserClient userClient;

    @InjectMocks private AdoptionService adoptionService;

    private Faker faker;
    private Adoption mockAdoption;
    private AdoptionStatus statusPending;
    private AdoptionStatus statusApproved;
    private AdoptionStatus statusRejected;
    private PetResponseDTO mockPet;
    private AdoptionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        faker = new Faker();

        // Seteamos los estados posibles
        statusPending = new AdoptionStatus(1L, "PENDING");
        statusApproved = new AdoptionStatus(2L, "APPROVED");
        statusRejected = new AdoptionStatus(3L, "REJECTED");

        // Lo seteamos sin dueño en primera instancia
        mockPet = new PetResponseDTO();
        mockPet.setId(10L);
        mockPet.setName("Firulais");
        mockPet.setOwnerId(null);

        mockAdoption = new Adoption();
        mockAdoption.setId(1L);
        mockAdoption.setRequestDate(LocalDateTime.now());
        mockAdoption.setNotes("Quiero adoptar a este perrito porque tengo patio grande.");
        mockAdoption.setAdoptionStatus(statusPending);
        mockAdoption.setPetId(10L);
        mockAdoption.setUserId(20L);

        requestDTO = new AdoptionRequestDTO(
                mockAdoption.getNotes(),
                mockAdoption.getPetId(),
                mockAdoption.getUserId()
        );
    }

    // Create
    @Test
    void create_ShouldReturnAdoptionDTO_WhenAllValidationsPass() {
        when(petClient.getPetById(requestDTO.getPetId())).thenReturn(mockPet);
        when(userClient.existById(requestDTO.getUserId())).thenReturn(true);
        when(userClient.isClient(requestDTO.getUserId())).thenReturn(true);
        when(adoptionStatusRepository.findByName("PENDING")).thenReturn(Optional.of(statusPending));
        when(adoptionRepository.save(any(Adoption.class))).thenReturn(mockAdoption);

        AdoptionResponseDTO result = adoptionService.create(requestDTO);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatusName());
        verify(petClient, times(1)).getPetById(anyLong());
        verify(userClient, times(1)).existById(anyLong());
        verify(userClient, times(1)).isClient(anyLong());
        verify(adoptionRepository, times(1)).save(any(Adoption.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenPetClientFails() {
        //Simulamos que ms-pets falla o no encuentra a la mascota
        when(petClient.getPetById(requestDTO.getPetId())).thenThrow(new RuntimeException("Not Found"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> adoptionService.create(requestDTO));
        assertEquals("La mascota con ID " + requestDTO.getPetId(), exception.getMessage());

        verify(userClient, never()).existById(anyLong());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenPetAlreadyHasOwner() {
        // Le asignamos un dueño a la mascota para simular que ya no está disponible
        mockPet.setOwnerId(99L);
        when(petClient.getPetById(requestDTO.getPetId())).thenReturn(mockPet);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> adoptionService.create(requestDTO));
        assertEquals("Error: La mascota Firulais ya tiene un dueño asignado", exception.getMessage());

        verify(userClient, never()).existById(anyLong());
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(petClient.getPetById(requestDTO.getPetId())).thenReturn(mockPet);
        when(userClient.existById(requestDTO.getUserId())).thenReturn(false); // Usuario no existe

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> adoptionService.create(requestDTO));
        assertEquals("El usuario con ID " + requestDTO.getUserId() + " no existe.", exception.getMessage());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenUserIsNotClient() {
        when(petClient.getPetById(requestDTO.getPetId())).thenReturn(mockPet);
        when(userClient.existById(requestDTO.getUserId())).thenReturn(true);
        when(userClient.isClient(requestDTO.getUserId())).thenReturn(false); // Es VET o ADMIN

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> adoptionService.create(requestDTO));
        assertEquals("Error: Solo los usuarios registrados como CLIENT pueden realizar solicitudes de adopción.", exception.getMessage());
    }

    //Aprove y reject
    @Test
    void approve_ShouldChangeStatusToApproved_AndUpdatePetOwner() {
        Long staffId = 30L;
        when(adoptionRepository.findById(1L)).thenReturn(Optional.of(mockAdoption));
        when(adoptionStatusRepository.findByName("APPROVED")).thenReturn(Optional.of(statusApproved));

        // Simulamos que al guardar, la entidad ya adquirió el estado APPROVED
        Adoption approvedAdoption = new Adoption();
        approvedAdoption.setId(1L);
        approvedAdoption.setAdoptionStatus(statusApproved);
        approvedAdoption.setPetId(mockAdoption.getPetId());
        approvedAdoption.setUserId(mockAdoption.getUserId());

        when(adoptionRepository.save(any(Adoption.class))).thenReturn(approvedAdoption);

        AdoptionResponseDTO result = adoptionService.approve(1L, staffId);

        assertNotNull(result);
        assertEquals("APPROVED", result.getStatusName());

        //Esta wea tenemos que mandar a verificar que si actualizo al dueño
        verify(petClient, times(1)).updateOwner(mockAdoption.getPetId(), mockAdoption.getUserId());
        verify(adoptionRepository, times(1)).save(any(Adoption.class));
    }

    @Test
    void reject_ShouldChangeStatusToRejected() {
        Long staffId = 30L;
        when(adoptionRepository.findById(1L)).thenReturn(Optional.of(mockAdoption));
        when(adoptionStatusRepository.findByName("REJECTED")).thenReturn(Optional.of(statusRejected));

        Adoption rejectedAdoption = new Adoption();
        rejectedAdoption.setId(1L);
        rejectedAdoption.setAdoptionStatus(statusRejected);

        when(adoptionRepository.save(any(Adoption.class))).thenReturn(rejectedAdoption);

        AdoptionResponseDTO result = adoptionService.reject(1L, staffId);

        assertNotNull(result);
        assertEquals("REJECTED", result.getStatusName());

        verify(petClient, never()).updateOwner(anyLong(), anyLong());
    }

    @Test
    void approve_ShouldThrowException_WhenStatusNotFound() {
        when(adoptionRepository.findById(1L)).thenReturn(Optional.of(mockAdoption));
        when(adoptionStatusRepository.findByName("APPROVED")).thenReturn(Optional.empty()); // No encuentra el estado en la BD

        assertThrows(ResourceNotFoundException.class, () -> adoptionService.approve(1L, 30L));
    }

    // Getters
    @Test
    void getAll_ShouldReturnListOfAdoptions() {
        when(adoptionRepository.findAll()).thenReturn(List.of(mockAdoption));
        List<AdoptionResponseDTO> result = adoptionService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void getAvailablePets_ShouldReturnPetsFromClient() {
        when(petClient.getAvailablePets()).thenReturn(List.of(mockPet));
        List<PetResponseDTO> result = adoptionService.getAvailablePets();
        assertEquals(1, result.size());
        verify(petClient, times(1)).getAvailablePets();
    }
}