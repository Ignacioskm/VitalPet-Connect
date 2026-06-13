package com.vitalpet.msstaff.service;

import com.vitalpet.msstaff.client.BranchClient;
import com.vitalpet.msstaff.client.UserClient;
import com.vitalpet.msstaff.dto.ScheduleRequestDTO;
import com.vitalpet.msstaff.dto.SpecialtyResponseDTO;
import com.vitalpet.msstaff.dto.StaffRequestDTO;
import com.vitalpet.msstaff.dto.StaffResponseDTO;
import com.vitalpet.msstaff.exception.ResourceNotFoundException;
import com.vitalpet.msstaff.model.Specialty;
import com.vitalpet.msstaff.model.Staff;
import com.vitalpet.msstaff.model.StaffSchedule;
import com.vitalpet.msstaff.repository.StaffRepository;
import com.vitalpet.msstaff.repository.StaffScheduleRepository;
import com.vitalpet.msstaff.repository.StaffSpecialty;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock private StaffScheduleRepository staffScheduleRepository;
    @Mock private StaffRepository staffRepository;
    @Mock private StaffSpecialty staffSpecialty;
    //Estos son los de OpenFeign
    @Mock private BranchClient branchClient;
    @Mock private UserClient userClient;

    @InjectMocks private StaffService staffService;

    //Variables globales
    private Faker faker;
    private Staff mockStaff;
    private Specialty mockSpecialty;
    private StaffRequestDTO requestDTO;
    private ScheduleRequestDTO scheduleRequestDTO;

    //Data faker
    @BeforeEach
    void setUp(){
        faker = new Faker();

        mockSpecialty = new Specialty();
        mockSpecialty.setId(1L);
        mockSpecialty.setName("VETERINARIAN");

        mockStaff = new Staff();
        mockStaff.setId(1L);
        mockStaff.setFirstName(faker.name().firstName());
        mockStaff.setLastName(faker.name().lastName());
        mockStaff.setEmail(faker.internet().emailAddress());
        mockStaff.setPhoneNumber(faker.phoneNumber().phoneNumber());
        mockStaff.setHireDate(LocalDate.now().minusDays(10)); // <- minus day resta días.
        mockStaff.setBranchId(2L);
        mockStaff.setUserId(3L);
        mockStaff.setSpecialty(mockSpecialty);
        mockStaff.setActive(true);

        StaffSchedule mockSchedule = new StaffSchedule();
        mockSchedule.setId(1L);
        mockSchedule.setDayOfWeek(DayOfWeek.MONDAY);
        mockSchedule.setStartTime(LocalTime.of(8,0));
        mockSchedule.setEndTime(LocalTime.of(17,0));
        mockSchedule.setStaff(mockStaff); // Vinculamos el horario al staff

        mockStaff.setSchedules(List.of(mockSchedule)); // Le entregamos la lista al mockStaff

        //DTOs de entrada
        scheduleRequestDTO = new ScheduleRequestDTO(
                DayOfWeek.MONDAY,
                LocalTime.of(8,0),
                LocalTime.of(17,0)
        );

        requestDTO = new StaffRequestDTO(
                mockStaff.getFirstName(),
                mockStaff.getFirstName(),
                mockStaff.getEmail(),
                mockStaff.getPhoneNumber(),
                mockStaff.getHireDate(),
                2L,
                3L,
                "VETERINARIAN",
                List.of(scheduleRequestDTO)
        );
    }

    //1. CREATE
    @Test
    void create_ShouldReturnStaffDTO_WhenAllValidationsPass() {
        // Simulamos respuestas ideales
        when(branchClient.existsById(requestDTO.getBranchId())).thenReturn(true);
        when(userClient.existById(requestDTO.getUserId())).thenReturn(true);
        when(userClient.isVet(requestDTO.getUserId())).thenReturn(true);

        when(staffRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(staffSpecialty.findByName(requestDTO.getSpecialtyName())).thenReturn(Optional.of(mockSpecialty));
        when(staffRepository.save(any(Staff.class))).thenReturn(mockStaff);

        StaffResponseDTO result = staffService.create(requestDTO);

        assertNotNull(result);
        assertEquals(mockStaff.getEmail(), result.getEmail());
        assertEquals(mockSpecialty.getName(), result.getSpecialtyName());
        assertFalse(result.getSchedules().isEmpty());

        verify(branchClient, times(1)).existsById(anyLong());
        verify(staffRepository, times(1)).save(any(Staff.class));
    }

    @Test
    void create_ShouldThrowRuntimeException_WhenBranchClientFails() {
        // Simula que el ms está apagado
        when(branchClient.existsById(requestDTO.getBranchId())).thenThrow(new RuntimeException("Connection Refused"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> staffService.create(requestDTO));
        assertEquals("Error de comunicación al validar la sucursal en ms-branchs", exception.getMessage());

        verify(userClient, never()).existById(anyLong()); // Nunca llega a consultar al usuario
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenBranchReturnsFalse() {
        // Sucursal devuelve false
        when(branchClient.existsById(requestDTO.getBranchId())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> staffService.create(requestDTO));
        assertEquals("Error: La sucursal con ID" + requestDTO.getBranchId() + "no existe", exception.getMessage());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenUserIsNotVet() {
        when(branchClient.existsById(requestDTO.getBranchId())).thenReturn(true);
        when(userClient.existById(requestDTO.getUserId())).thenReturn(true);
        // Si existe pero no es VET
        when(userClient.isVet(requestDTO.getUserId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> staffService.create(requestDTO));
        assertEquals("Error: El usuario debe tener el rol VET para ser registrado como personal médico.", exception.getMessage());
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenScheduleIsInvalid() {
        when(branchClient.existsById(requestDTO.getBranchId())).thenReturn(true);
        when(userClient.existById(requestDTO.getUserId())).thenReturn(true);
        when(userClient.isVet(requestDTO.getUserId())).thenReturn(true);
        when(staffRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(staffSpecialty.findByName(requestDTO.getSpecialtyName())).thenReturn(Optional.of(mockSpecialty));

        // Aca metemos un horario raro, que termine antes de empezar
        requestDTO.getSchedules().get(0).setStartTime(LocalTime.of(18, 0));
        requestDTO.getSchedules().get(0).setEndTime(LocalTime.of(8, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> staffService.create(requestDTO));
        assertTrue(exception.getMessage().contains("La hora de inicio no puede ser posterior a la hora termino"));
    }

    //2. Branches
    @Test
    void findStaffByBranchId_ShouldReturnList_WhenBranchExists() {
        when(branchClient.existsById(2L)).thenReturn(true);
        when(staffRepository.findByBranchId(2L)).thenReturn(List.of(mockStaff));

        List<StaffResponseDTO> result = staffService.findStaffByBranchId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(branchClient, times(1)).existsById(2L);
        verify(staffRepository, times(1)).findByBranchId(2L);
    }

    @Test
    void findStaffByBranchId_ShouldThrowResourceNotFound_WhenBranchReturnsFalse() {
        when(branchClient.existsById(2L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> staffService.findStaffByBranchId(2L));
        assertEquals("Error: La sucursal con ID2no existe", exception.getMessage()); // Tal cual está en tu código
        verify(staffRepository, never()).findByBranchId(anyLong());
    }

    //3. CRUD
    @Test
    void getAll_ShouldReturnListOfStaff() {
        when(staffRepository.findByActiveTrue()).thenReturn(List.of(mockStaff));
        List<StaffResponseDTO> result = staffService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void getAllSpecialties_ShouldReturnList() {
        when(staffSpecialty.findAll()).thenReturn(List.of(mockSpecialty));
        List<SpecialtyResponseDTO> result = staffService.getAllSpecialties();
        assertEquals(1, result.size());
    }

    @Test
    void getById_ShouldReturnStaff_WhenExists() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(mockStaff));
        StaffResponseDTO result = staffService.getById(1L);
        assertEquals(mockStaff.getEmail(), result.getEmail());
    }

    @Test
    void update_ShouldUpdateAndReturnStaff() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(mockStaff));
        when(staffSpecialty.findByName(requestDTO.getSpecialtyName())).thenReturn(Optional.of(mockSpecialty));
        when(staffRepository.save(any(Staff.class))).thenReturn(mockStaff);

        StaffResponseDTO result = staffService.update(1L, requestDTO);

        assertNotNull(result);
        verify(staffRepository, times(1)).save(mockStaff);
    }

    @Test
    void desactivate_ShouldSetActiveFalse() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(mockStaff));

        staffService.desactivate(1L);

        assertFalse(mockStaff.getActive());
        verify(staffRepository, times(1)).save(mockStaff);
    }

    @Test
    void staffExistsById_ShouldReturnBoolean() {
        when(staffRepository.existsById(1L)).thenReturn(true);
        assertTrue(staffService.staffExistsById(1L));
    }

}