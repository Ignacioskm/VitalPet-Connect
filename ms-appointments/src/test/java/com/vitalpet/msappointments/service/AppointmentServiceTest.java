package com.vitalpet.msappointments.service;

import com.vitalpet.msappointments.client.BranchClient;
import com.vitalpet.msappointments.client.PaymentClient;
import com.vitalpet.msappointments.client.PetClient;
import com.vitalpet.msappointments.client.StaffClient;
import com.vitalpet.msappointments.dto.*;
import com.vitalpet.msappointments.exception.ResourceNotFoundException;
import com.vitalpet.msappointments.model.Appointment;
import com.vitalpet.msappointments.model.AppointmentStatus;
import com.vitalpet.msappointments.model.MedicalService;
import com.vitalpet.msappointments.repository.AppointmentRepository;
import com.vitalpet.msappointments.repository.AppointmentStatusRepository;
import com.vitalpet.msappointments.repository.MedicalServiceRepository;
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
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private AppointmentStatusRepository appointmentStatusRepository;
    @Mock private MedicalServiceRepository medicalServiceRepository;

    @Mock private PetClient petClient;
    @Mock private StaffClient staffClient;
    @Mock private BranchClient branchClient;
    @Mock private PaymentClient paymentClient;

    @InjectMocks private AppointmentService appointmentService;

    private Faker faker;
    private Appointment mockAppointment;
    private MedicalService mockMedicalService;
    private AppointmentStatus statusPending;
    private AppointmentStatus statusConfirmed;
    private AppointmentStatus statusCompleted;
    private AppointmentRequestDTO requestDTO;
    private PetResponseDTO mockPetResponse;

    @BeforeEach
    void setUp() {
        faker = new Faker();

        // 1. Estados
        statusPending = new AppointmentStatus(1L, "PENDING");
        statusConfirmed = new AppointmentStatus(2L, "CONFIRMED");
        statusCompleted = new AppointmentStatus(3L, "COMPLETED");

        // 2. Servicio Médico
        mockMedicalService = new MedicalService(1L, "Consulta General", 25000.0);

        // 3. Cita base
        mockAppointment = new Appointment();
        mockAppointment.setId(1L);
        mockAppointment.setScheduledAt(LocalDateTime.now().plusDays(2));
        mockAppointment.setMedicalService(mockMedicalService);
        mockAppointment.setNotes("Primera vacuna");
        mockAppointment.setAppointmentStatus(statusPending);
        mockAppointment.setPetId(10L);
        mockAppointment.setStaffId(20L);
        mockAppointment.setBranchId(30L);

        // 4. Request DTO
        requestDTO = new AppointmentRequestDTO(
                mockAppointment.getScheduledAt(),
                mockMedicalService.getId(),
                mockAppointment.getNotes(),
                mockAppointment.getPetId(),
                mockAppointment.getStaffId(),
                mockAppointment.getBranchId()
        );

        // 5. Respuesta simulada del PetClient
        mockPetResponse = new PetResponseDTO();
        mockPetResponse.setId(10L);
        mockPetResponse.setOwnerId(99L); // ID del dueño que pagará
    }

    // Create
    @Test
    void create_ShouldReturnAppointmentDTO_WhenValidationsPass() {
        when(petClient.existsById(requestDTO.getPetId())).thenReturn(true);
        when(staffClient.existsById(requestDTO.getStaffId())).thenReturn(true);
        when(branchClient.existsById(requestDTO.getBranchId())).thenReturn(true);

        when(appointmentRepository.existsByStaffIdAndScheduledAt(requestDTO.getStaffId(), requestDTO.getScheduledAt())).thenReturn(false);

        when(medicalServiceRepository.findById(requestDTO.getMedicalServiceId())).thenReturn(Optional.of(mockMedicalService));
        when(appointmentStatusRepository.findByName("PENDING")).thenReturn(Optional.of(statusPending));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

        AppointmentResponseDTO result = appointmentService.create(requestDTO);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatusName());

        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenScheduleConflict() {
        when(petClient.existsById(anyLong())).thenReturn(true);
        when(staffClient.existsById(anyLong())).thenReturn(true);
        when(branchClient.existsById(anyLong())).thenReturn(true);

        // Simulamos que el vet ya está ocupado a esa hora
        when(appointmentRepository.existsByStaffIdAndScheduledAt(requestDTO.getStaffId(), requestDTO.getScheduledAt())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentService.create(requestDTO));
        assertEquals("El vet ya tiene una cita para este horario", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowResourceNotFound_WhenPetClientReturnsFalse() {
        when(petClient.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(requestDTO));

        // Fail Fast: No debe consultar al staff ni branch si la mascota falló
        verify(staffClient, never()).existsById(anyLong());
        verify(branchClient, never()).existsById(anyLong());
    }

    //Change
    @Test
    void changeStatus_ShouldUpdateStatus() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));
        when(appointmentStatusRepository.findByName("CONFIRMED")).thenReturn(Optional.of(statusConfirmed));

        Appointment confirmedApp = mockAppointment;
        confirmedApp.setAppointmentStatus(statusConfirmed);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(confirmedApp);

        AppointmentResponseDTO result = appointmentService.changeStatus(1L, "CONFIRMED");

        assertEquals("CONFIRMED", result.getStatusName());
    }

    //Complete
    @Test
    void completeAppointment_ShouldChangeToCompleted_AndCreatePayment() {
        mockAppointment.setAppointmentStatus(statusConfirmed); // Nace confirmada

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));
        when(appointmentStatusRepository.findByName("COMPLETED")).thenReturn(Optional.of(statusCompleted));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);
        when(petClient.getPetById(mockAppointment.getPetId())).thenReturn(mockPetResponse);

        AppointmentResponseDTO result = appointmentService.completeAppointment(1L);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatusName());

        verify(paymentClient, times(1)).createPayment(any(PaymentRequestDTO.class));
    }

    @Test
    void completeAppointment_ShouldThrowIllegalStateException_WhenNotConfirmed() {

        mockAppointment.setAppointmentStatus(statusPending);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> appointmentService.completeAppointment(1L));
        assertTrue(exception.getMessage().contains("Solo se pueden completar y cobrar citas que estén previamente en estado CONFIRMED"));

        verify(paymentClient, never()).createPayment(any());
    }

    @Test
    void completeAppointment_ShouldThrowResourceNotFound_WhenPetClientFails() {
        mockAppointment.setAppointmentStatus(statusConfirmed);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));
        when(appointmentStatusRepository.findByName("COMPLETED")).thenReturn(Optional.of(statusCompleted));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

        // Simulamos falla al buscar la mascota
        when(petClient.getPetById(anyLong())).thenThrow(new RuntimeException("Timeout"));

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.completeAppointment(1L));
        verify(paymentClient, never()).createPayment(any());
    }

    @Test
    void completeAppointment_ShouldReturnDTO_EvenIfPaymentClientFails() {

        mockAppointment.setAppointmentStatus(statusConfirmed);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));
        when(appointmentStatusRepository.findByName("COMPLETED")).thenReturn(Optional.of(statusCompleted));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);
        when(petClient.getPetById(anyLong())).thenReturn(mockPetResponse);

        // Simulamos que el microservicio de pagos está caído
        doThrow(new RuntimeException("Servicio de pagos inalcanzable")).when(paymentClient).createPayment(any());

        AppointmentResponseDTO result = appointmentService.completeAppointment(1L);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatusName());
    }

    //Getters
    @Test
    void getAll_ShouldReturnList() {
        when(appointmentRepository.findAll()).thenReturn(List.of(mockAppointment));
        List<AppointmentResponseDTO> result = appointmentService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void getAllMedicalServices_ShouldReturnList() {
        when(medicalServiceRepository.findAll()).thenReturn(List.of(mockMedicalService));
        List<MedicalServiceResponseDTO> result = appointmentService.getAllMedicalServices();
        assertEquals(1, result.size());
    }

    @Test
    void getByStaff_ShouldReturnList() {
        when(appointmentRepository.findByStaffIdOrderByScheduledAtAsc(20L)).thenReturn(List.of(mockAppointment));
        List<AppointmentResponseDTO> result = appointmentService.getByStaff(20L);
        assertEquals(1, result.size());
    }
}