package com.vitalpet.msappointments.service;

import com.vitalpet.msappointments.client.BranchClient;
import com.vitalpet.msappointments.client.PaymentClient;
import com.vitalpet.msappointments.client.PetClient;
import com.vitalpet.msappointments.client.StaffClient;
import com.vitalpet.msappointments.dto.AppointmentRequestDTO;
import com.vitalpet.msappointments.dto.AppointmentResponseDTO;
import com.vitalpet.msappointments.model.Appointment;
import com.vitalpet.msappointments.model.AppointmentStatus;
import com.vitalpet.msappointments.repository.AppointmentRepository;
import com.vitalpet.msappointments.repository.AppointmentStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    //Repositorios
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private AppointmentStatusRepository appointmentStatusRepository;

    //Clientes OpenFeign
    @Autowired private PetClient petClient;
    @Autowired private StaffClient staffClient;
    @Autowired private BranchClient branchClient;
    @Autowired private PaymentClient paymentClient;

    public AppointmentResponseDTO create(AppointmentRequestDTO dto){

        //Validamos lo de otros ms.

        if(!petClient.existsById(dto.getPetId())) throw new RuntimeException("Mascota no existe");
        if(!staffClient.existsById(dto.getStaffId())) throw new RuntimeException("Staff no existe");
        if(!branchClient.existsById(dto.getBranchId())) throw new RuntimeException("Sucursal no existe");

        //Validamos si el horario está ocupado.
        if(appointmentRepository.existsByStaffIdAndScheduledAt(dto.getStaffId(), dto.getScheduledAt())){
            throw new RuntimeException("El vet ya tiene una cita para este horario");
        }

        //Seteamos el estado inicial del pedido (PENDIENTE)
        AppointmentStatus appointmentStatus = appointmentStatusRepository.findByName("PENDING")
                .orElseThrow(() -> new RuntimeException("Estado PENDING no encontrado"));

        //Mapeamos y guardamos
        Appointment appointment = new Appointment();
        appointment.setScheduledAt(dto.getScheduledAt());
        appointment.setReason(dto.getReason());
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

    //Traer appointments por staff
    public List<AppointmentResponseDTO> getByStaff(Long staffId){
        return appointmentRepository.findByStaffIdOrderByScheduledAtAsc(staffId)
                .stream().map(this::toDTO).toList();
    }

    //Cambiar de estado PEND -> CONFIRMED,CANCELED,COMPLETED.
    //Completed tiene que ser validad con si se pagó o no asi que de momento lo dejaré asi.
    public AppointmentResponseDTO changeStatus(Long id, String newStatus){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new RuntimeException("Cita no encontrada"));

        AppointmentStatus status = appointmentStatusRepository.findByName(newStatus).orElseThrow(() -> new RuntimeException("Estado" + newStatus + " No Encontrado"));

        appointment.setAppointmentStatus(status);

        return toDTO(appointmentRepository.save(appointment));
    }



    //MAPEADOR
    private AppointmentResponseDTO toDTO(Appointment app){
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(app.getId());
        dto.setScheduledAt(app.getScheduledAt());
        dto.setReason(app.getReason());
        dto.setNotes(app.getNotes());
        dto.setCreatedAt(app.getCreatedAt());
        dto.setStatusName(app.getAppointmentStatus().getName());
        dto.setPetId(dto.getPetId());
        dto.setStaffId(dto.getStaffId());
        dto.setBranchId(dto.getBranchId());

        return dto;
    }
}
