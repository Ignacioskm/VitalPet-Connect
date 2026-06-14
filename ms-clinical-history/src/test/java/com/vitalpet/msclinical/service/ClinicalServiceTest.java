package com.vitalpet.msclinical.service;

import com.vitalpet.msclinical.client.PetClient;
import com.vitalpet.msclinical.client.StaffClient;
import com.vitalpet.msclinical.dto.ClinicalRecordRequestDTO;
import com.vitalpet.msclinical.dto.ClinicalRecordResponseDTO;
import com.vitalpet.msclinical.dto.PrescriptionRequestDTO;
import com.vitalpet.msclinical.exception.ResourceNotFoundException;
import com.vitalpet.msclinical.model.ClinicalRecord;
import com.vitalpet.msclinical.model.Prescription;
import com.vitalpet.msclinical.repository.ClinicalRecordRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicalServiceTest {

    @Mock private ClinicalRecordRepository clinicalRepository;
    @Mock private PetClient petClient;
    @Mock private StaffClient staffClient;

    @InjectMocks private ClinicalService clinicalService;

    private Faker faker;
    private ClinicalRecord mockClinicalRecord;
    private Prescription mockPrescription;
    private ClinicalRecordRequestDTO requestDTO;
    private PrescriptionRequestDTO prescriptionRequestDTO;

    @BeforeEach
    void setUp() {
        faker = new Faker();

        mockClinicalRecord = new ClinicalRecord();
        mockClinicalRecord.setId(1L);
        mockClinicalRecord.setVisitDate(LocalDate.now());
        mockClinicalRecord.setReason("Control de rutina");
        mockClinicalRecord.setDiagnosis("Parásitos intestinales");
        mockClinicalRecord.setTreatment("Desparasitación interna");
        mockClinicalRecord.setNotes("Volver en 15 días");
        mockClinicalRecord.setPetId(10L);
        mockClinicalRecord.setStaffId(20L);

        mockPrescription = new Prescription();
        mockPrescription.setId(1L);
        mockPrescription.setMedicationName(faker.medical().medicineName());
        mockPrescription.setDosage("1 pastilla cada 12 horas");
        mockPrescription.setDurationDays(5);
        mockPrescription.setClinicalRecord(mockClinicalRecord);

        mockClinicalRecord.setPrescriptions(List.of(mockPrescription));

        prescriptionRequestDTO = new PrescriptionRequestDTO(
                mockPrescription.getMedicationName(),
                mockPrescription.getDosage(),
                mockPrescription.getDurationDays()
        );

        requestDTO = new ClinicalRecordRequestDTO(
                mockClinicalRecord.getPetId(),
                mockClinicalRecord.getStaffId(),
                mockClinicalRecord.getReason(),
                mockClinicalRecord.getDiagnosis(),
                mockClinicalRecord.getTreatment(),
                mockClinicalRecord.getNotes(),
                new ArrayList<>(List.of(prescriptionRequestDTO)) // Lista mutable
        );
    }


    // Para los Create
    @Test
    void create_ShouldReturnClinicalRecordDTO_WhenValidationsPass_AndHasPrescriptions() {

        when(petClient.existsById(requestDTO.getPetId())).thenReturn(true);
        when(staffClient.existsById(requestDTO.getStaffId())).thenReturn(true);
        when(clinicalRepository.save(any(ClinicalRecord.class))).thenReturn(mockClinicalRecord);

        ClinicalRecordResponseDTO result = clinicalService.create(requestDTO);

        assertNotNull(result);
        assertEquals("Control de rutina", result.getReason());
        assertFalse(result.getPrescriptions().isEmpty()); // Validamos que mapeó la receta
        assertEquals(mockPrescription.getMedicationName(), result.getPrescriptions().get(0).getMedicationName());

        verify(petClient, times(1)).existsById(requestDTO.getPetId());
        verify(staffClient, times(1)).existsById(requestDTO.getStaffId());
        verify(clinicalRepository, times(1)).save(any(ClinicalRecord.class));
    }

    @Test
    void create_ShouldReturnClinicalRecordDTO_WhenValidationsPass_AndNoPrescriptions() {
        requestDTO.setPrescriptions(null);
        mockClinicalRecord.setPrescriptions(null);

        when(petClient.existsById(requestDTO.getPetId())).thenReturn(true);
        when(staffClient.existsById(requestDTO.getStaffId())).thenReturn(true);
        when(clinicalRepository.save(any(ClinicalRecord.class))).thenReturn(mockClinicalRecord);

        ClinicalRecordResponseDTO result = clinicalService.create(requestDTO);

        assertNotNull(result);
        assertEquals("Control de rutina", result.getReason());
        assertNull(result.getPrescriptions()); // Validamos que quedó nulo limpiamente sin lanzar NullPointer

        verify(clinicalRepository, times(1)).save(any(ClinicalRecord.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenPetDoesNotExist() {
        when(petClient.existsById(requestDTO.getPetId())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> clinicalService.create(requestDTO));
        assertEquals("Error: La mascota con ID " + requestDTO.getPetId() + " no existe", exception.getMessage());

        verify(staffClient, never()).existsById(anyLong());
        verify(clinicalRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenStaffDoesNotExist() {
        when(petClient.existsById(requestDTO.getPetId())).thenReturn(true);
        when(staffClient.existsById(requestDTO.getStaffId())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> clinicalService.create(requestDTO));
        assertEquals("Error: El funcionario con ID " + requestDTO.getStaffId() + " no existe", exception.getMessage());

        verify(clinicalRepository, never()).save(any());
    }

    //Para los Get
    @Test
    void getHistoryByPet_ShouldReturnListOfRecords() {
        Long petId = 10L;
        when(clinicalRepository.findByPetIdOrderByVisitDateDesc(petId)).thenReturn(List.of(mockClinicalRecord));

        List<ClinicalRecordResponseDTO> result = clinicalService.getHistoryByPet(petId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Control de rutina", result.get(0).getReason());
        verify(clinicalRepository, times(1)).findByPetIdOrderByVisitDateDesc(petId);
    }

    @Test
    void getByID_ShouldReturnClinicalRecordDTO_WhenExists() {
        Long validId = 1L;
        when(clinicalRepository.findById(validId)).thenReturn(Optional.of(mockClinicalRecord));

        ClinicalRecordResponseDTO result = clinicalService.getByID(validId);

        assertNotNull(result);
        assertEquals(mockClinicalRecord.getId(), result.getId());
        verify(clinicalRepository, times(1)).findById(validId);
    }

    @Test
    void getByID_ShouldThrowResourceNotFoundException_WhenNotExists() {
        Long invalidId = 99L;
        when(clinicalRepository.findById(invalidId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> clinicalService.getByID(invalidId));
        assertEquals("Clínica no encontrada.", exception.getMessage());
        verify(clinicalRepository, times(1)).findById(invalidId);
    }
}