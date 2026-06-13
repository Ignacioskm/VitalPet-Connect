package com.vitalpet.mspets.service;

import com.vitalpet.mspets.client.UserClient;
import com.vitalpet.mspets.dto.PetRequestDTO;
import com.vitalpet.mspets.dto.PetResponseDTO;
import com.vitalpet.mspets.dto.SpeciesResponseDTO;
import com.vitalpet.mspets.exception.ResourceNotFoundException;
import com.vitalpet.mspets.model.Pet;
import com.vitalpet.mspets.model.Species;
import com.vitalpet.mspets.repository.PetRepository;
import com.vitalpet.mspets.repository.SpeciesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock private PetRepository petRepository;
    @Mock private SpeciesRepository speciesRepository;
    @Mock private UserClient userClient;

    @InjectMocks private PetService petService;

    private Faker faker;
    private Pet mockPet;
    private Species mockSpecies;
    private PetRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        faker = new Faker();

        mockSpecies = new Species();
        mockSpecies.setId(1L);
        mockSpecies.setName("Dog");

        mockPet = new Pet();
        mockPet.setId(1L);
        mockPet.setName(faker.dog().name());
        mockPet.setBreed(faker.dog().breed());
        mockPet.setBirthDate(LocalDate.now().minusYears(2));
        mockPet.setWeight(15.5);
        mockPet.setActive(true);
        mockPet.setSpecies(mockSpecies);
        mockPet.setOwnerId(2L); // Simula que pertenece al usuario con ID 2

        requestDTO = new PetRequestDTO(
                mockPet.getName(),
                mockPet.getBreed(),
                mockPet.getBirthDate(),
                mockPet.getWeight(),
                mockSpecies.getId(),
                mockPet.getOwnerId()
        );
    }

    // Los voy a ordenar por Get primero
    @Test
    void getAvailablePets_ShouldReturnPetsWithoutOwner() {
        when(petRepository.findByOwnerIdIsNull()).thenReturn(List.of(mockPet));
        List<PetResponseDTO> result = petService.getAvailablePets();
        assertEquals(1, result.size());
        verify(petRepository, times(1)).findByOwnerIdIsNull();
    }

    @Test
    void getAllSpecies_ShouldReturnSpeciesList() {
        when(speciesRepository.findAll()).thenReturn(List.of(mockSpecies));
        List<SpeciesResponseDTO> result = petService.getAllSpecies();
        assertEquals(1, result.size());
        assertEquals("Dog", result.get(0).getName());
    }

    @Test
    void getAll_ShouldReturnActivePets() {
        when(petRepository.findByActiveTrue()).thenReturn(List.of(mockPet));
        List<PetResponseDTO> result = petService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void getById_ShouldReturnPet_WhenExists() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(mockPet));
        PetResponseDTO result = petService.getById(1L);
        assertNotNull(result);
        assertEquals(mockPet.getName(), result.getName());
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> petService.getById(99L));
    }

    // Ahora los create
    @Test
    void create_ShouldReturnPetDTO_WhenOwnerIsNull() {
        //Perro sin dueño ):
        requestDTO.setOwnerId(null);
        when(speciesRepository.findById(requestDTO.getSpeciesId())).thenReturn(Optional.of(mockSpecies));
        when(petRepository.save(any(Pet.class))).thenReturn(mockPet);

        PetResponseDTO result = petService.create(requestDTO);

        //Como es nulo no debería llamar al ms
        assertNotNull(result);
        verify(userClient, never()).existById(anyLong());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void create_ShouldReturnPetDTO_WhenOwnerIsValidClient() {
        // Este es con dueño, tonces validamos especia y usuario por OpenFeign
        when(speciesRepository.findById(requestDTO.getSpeciesId())).thenReturn(Optional.of(mockSpecies));
        when(userClient.existById(requestDTO.getOwnerId())).thenReturn(true);
        when(userClient.isClient(requestDTO.getOwnerId())).thenReturn(true);
        when(petRepository.save(any(Pet.class))).thenReturn(mockPet);

        PetResponseDTO result = petService.create(requestDTO);

        assertNotNull(result);
        verify(userClient, times(1)).existById(requestDTO.getOwnerId());
        verify(userClient, times(1)).isClient(requestDTO.getOwnerId());
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenSpeciesDoesNotExist() {
        when(speciesRepository.findById(requestDTO.getSpeciesId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> petService.create(requestDTO));
        verify(petRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenOwnerIsNotClient() {
        when(speciesRepository.findById(requestDTO.getSpeciesId())).thenReturn(Optional.of(mockSpecies));
        when(userClient.existById(requestDTO.getOwnerId())).thenReturn(true);
        when(userClient.isClient(requestDTO.getOwnerId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> petService.create(requestDTO));
        assertEquals("Error: El user no es un cliente.", exception.getMessage());
        verify(petRepository, never()).save(any());
    }

    //Esto para los update
    @Test
    void update_ShouldReturnUpdatedPetDTO() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(mockPet));
        when(speciesRepository.findById(requestDTO.getSpeciesId())).thenReturn(Optional.of(mockSpecies));
        when(userClient.existById(requestDTO.getOwnerId())).thenReturn(true);
        when(userClient.isClient(requestDTO.getOwnerId())).thenReturn(true);
        when(petRepository.save(any(Pet.class))).thenReturn(mockPet);

        PetResponseDTO result = petService.update(1L, requestDTO);

        assertNotNull(result);
        verify(petRepository, times(1)).save(mockPet);
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenOwnerDoesNotExist() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(mockPet));
        when(speciesRepository.findById(requestDTO.getSpeciesId())).thenReturn(Optional.of(mockSpecies));
        when(userClient.existById(requestDTO.getOwnerId())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> petService.update(1L, requestDTO));
        verify(petRepository, never()).save(any());
    }

    //El resto de weas
    @Test
    void deactivate_ShouldSetActiveFalse() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(mockPet));
        petService.deactivate(1L);
        assertFalse(mockPet.getActive());
        verify(petRepository, times(1)).save(mockPet);
    }

    @Test
    void getByOwnerId_ShouldReturnList() {
        when(petRepository.findByOwnerId(2L)).thenReturn(List.of(mockPet));
        List<PetResponseDTO> result = petService.getByOwnerId(2L);
        assertEquals(1, result.size());
    }

    @Test
    void getBySpeciesName_ShouldReturnList() {
        when(petRepository.findBySpeciesName("Dog")).thenReturn(List.of(mockPet));
        List<PetResponseDTO> result = petService.getBySpeciesName("Dog");
        assertEquals(1, result.size());
    }

    @Test
    void petExists_ShouldReturnBoolean() {
        when(petRepository.existsPetById(1L)).thenReturn(true);
        assertTrue(petService.petExists(1L));
    }

    //Asignar dueño
    @Test
    void assignOwner_ShouldUpdateOwnerId_WhenUserIsClient() {
        Long newOwnerId = 5L;
        when(petRepository.findById(1L)).thenReturn(Optional.of(mockPet));
        when(userClient.existById(newOwnerId)).thenReturn(true);
        when(userClient.isClient(newOwnerId)).thenReturn(true);

        petService.assignOwner(1L, newOwnerId);

        assertEquals(newOwnerId, mockPet.getOwnerId());
        verify(petRepository, times(1)).save(mockPet);
    }

    @Test
    void assignOwner_ShouldThrowIllegalArgumentException_WhenUserIsNotClient() {
        Long newOwnerId = 5L;
        when(petRepository.findById(1L)).thenReturn(Optional.of(mockPet));
        when(userClient.existById(newOwnerId)).thenReturn(true);
        when(userClient.isClient(newOwnerId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> petService.assignOwner(1L, newOwnerId));
        assertEquals("El usuario debe tener el rol de cliente para ser dueño de una mascota.", exception.getMessage());

        verify(petRepository, never()).save(any()); //Nota para mí: Recordar verificar que no se guarde.
    }
}