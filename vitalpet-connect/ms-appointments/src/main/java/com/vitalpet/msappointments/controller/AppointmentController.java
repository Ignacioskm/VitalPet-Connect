package com.vitalpet.msappointments.controller;

import com.vitalpet.msappointments.dto.AppointmentRequestDTO;
import com.vitalpet.msappointments.dto.AppointmentResponseDTO;
import com.vitalpet.msappointments.dto.MedicalServiceResponseDTO;
import com.vitalpet.msappointments.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> create(@RequestBody AppointmentRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAll(){return ResponseEntity.ok(appointmentService.getAll());}

    @GetMapping("/medicals-services")
    public ResponseEntity<List<MedicalServiceResponseDTO>> getAllMedicals(){return ResponseEntity.ok(appointmentService.getAllMedicalServices());}

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getByStaff(@PathVariable Long staffId){
        return ResponseEntity.ok(appointmentService.getByStaff(staffId));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponseDTO> confirm(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.changeStatus(id,"CONFIRMED"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancel(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.changeStatus(id,"CANCELLED"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDTO> complete(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }
}
