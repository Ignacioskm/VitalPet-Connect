package com.vitalpet.msappointments.repository;

import com.vitalpet.msappointments.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    //Verificamos que exista una cita disponible para este veterinario a esta hora.
    boolean existsByStaffIdAndScheduledAt(Long staffId, LocalDateTime scheduleAt);

    //Para ver la agenda del vet
    //Recordemos que jpa va a buscar solito por staff y ordenar los horarios de manera asc del staff.
    List<Appointment> findByStaffIdOrderByScheduledAtAsc(Long staffId);
}
