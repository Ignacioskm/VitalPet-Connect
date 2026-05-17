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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    //Repositorios
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private AppointmentStatusRepository appointmentStatusRepository;
    @Autowired private MedicalServiceRepository medicalServiceRepository;

    //Clientes OpenFeign
    @Autowired private PetClient petClient;
    @Autowired private StaffClient staffClient;
    @Autowired private BranchClient branchClient;
    @Autowired private PaymentClient paymentClient;

    public AppointmentResponseDTO create(AppointmentRequestDTO dto){

        //Validamos lo de otros ms.
        if(!petClient.existsById(dto.getPetId())) throw new ResourceNotFoundException("Mascota no existe");
        if(!staffClient.existsById(dto.getStaffId())) throw new ResourceNotFoundException("Staff no existe");
        if(!branchClient.existsById(dto.getBranchId())) throw new ResourceNotFoundException("Sucursal no existe");

        //Validamos si el horario está ocupado.
        if(appointmentRepository.existsByStaffIdAndScheduledAt(dto.getStaffId(), dto.getScheduledAt())){
            throw new IllegalArgumentException("El vet ya tiene una cita para este horario");
        }

        MedicalService medicalService = medicalServiceRepository.findById(dto.getMedicalServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio médico no encontrado."));

        //Seteamos el estado inicial del pedido (PENDIENTE)
        AppointmentStatus appointmentStatus = appointmentStatusRepository.findByName("PENDING")
                .orElseThrow(() -> new ResourceNotFoundException("Estado PENDING no encontrado"));

        //Mapeamos y guardamos
        Appointment appointment = new Appointment();
        appointment.setScheduledAt(dto.getScheduledAt());
        appointment.setMedicalService(medicalService);
        appointment.setNotes(dto.getNotes());
        appointment.setPetId(dto.getPetId());
        appointment.setStaffId(dto.getStaffId());
        appointment.setBranchId(dto.getBranchId());
        appointment.setAppointmentStatus(appointmentStatus); //Le seteamos el estado PENDIENTE que es nuestro estado por defecto

        return toDTO(appointmentRepository.save(appointment));
    }

    //Traer todos los appointments
    public List<AppointmentResponseDTO> getAll(){
        return appointmentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MedicalServiceResponseDTO> getAllMedicalServices(){
        return medicalServiceRepository.findAll().stream().map(this::toMsDTO).toList();
    }

    //Traer appointments por staff
    public List<AppointmentResponseDTO> getByStaff(Long staffId){
        return appointmentRepository.findByStaffIdOrderByScheduledAtAsc(staffId)
                .stream().map(this::toDTO).toList();
    }

    //Cambiar de estado PEND -> CONFIRMED,CANCELED,COMPLETED.
    //Completed tiene que ser validad con si se pagó o no asi que de momento lo dejaré asi.
    public AppointmentResponseDTO changeStatus(Long id, String newStatus){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Cita no encontrada"));

        AppointmentStatus status = appointmentStatusRepository.findByName(newStatus)
                .orElseThrow(() -> new ResourceNotFoundException("Estado" + newStatus + " No Encontrado"));

        appointment.setAppointmentStatus(status);

        return toDTO(appointmentRepository.save(appointment));
    }

    //complete (terminar pago)
    public AppointmentResponseDTO completeAppointment(Long id){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        if (!appointment.getAppointmentStatus().getName().equals("CONFIRMED")) {
            throw new IllegalStateException("Error de negocio: Solo se pueden completar y cobrar citas que estén previamente en estado CONFIRMED.");
        }

        AppointmentStatus appointmentStatus = appointmentStatusRepository.findByName("COMPLETED")
                .orElseThrow(()-> new ResourceNotFoundException("Estado COMPLETED no encontrado"));

        appointment.setAppointmentStatus(appointmentStatus);
        Appointment saveAppointment = appointmentRepository.save(appointment);

        //Aquí hay que ver como hacemos el pago en payments
        //buscamos la mascota para ver quien es el dueño
        PetResponseDTO pet;
        try {
            pet = petClient.getPetById(saveAppointment.getPetId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error: No se pudo obtener la información de la mascota desde ms-pets.");
        }

        PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
        paymentRequest.setAmount(saveAppointment.getMedicalService().getPrice());
        paymentRequest.setUserId(pet.getOwnerId());
        paymentRequest.setAppointmentId(saveAppointment.getId());

        //Enviamos el cobro
        try {
            paymentClient.createPayment(paymentRequest);
        } catch (Exception e) {
            System.err.println("Error al generar el pago en ms-payments: " + e.getMessage());
        }

        return toDTO(saveAppointment);
    }

    //MAPEADOR
    private AppointmentResponseDTO toDTO(Appointment app){
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(app.getId());
        dto.setScheduledAt(app.getScheduledAt());
        dto.setMedicalServiceName(app.getMedicalService().getName());
        dto.setPrice(app.getMedicalService().getPrice());
        dto.setNotes(app.getNotes());
        dto.setCreatedAt(app.getCreatedAt());
        dto.setStatusName(app.getAppointmentStatus().getName());

        dto.setPetId(app.getPetId());
        dto.setStaffId(app.getStaffId());
        dto.setBranchId(app.getBranchId());

        return dto;
    }

    private MedicalServiceResponseDTO toMsDTO(MedicalService medicalService){
        MedicalServiceResponseDTO dto = new MedicalServiceResponseDTO();
        dto.setId(medicalService.getId());
        dto.setName(medicalService.getName());
        dto.setPrice(medicalService.getPrice());
        return dto;
    }
}
