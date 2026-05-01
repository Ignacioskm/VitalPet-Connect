package com.vitalpet.msappointments.repository;

import com.vitalpet.msappointments.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentStatusRepository extends JpaRepository<AppointmentStatus,Long> {
    Optional<AppointmentStatus> findByName(String name);
}
