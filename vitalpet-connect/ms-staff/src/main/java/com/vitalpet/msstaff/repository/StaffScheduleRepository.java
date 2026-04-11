package com.vitalpet.msstaff.repository;

import com.vitalpet.msstaff.model.Staff;
import com.vitalpet.msstaff.model.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffScheduleRepository extends JpaRepository<StaffSchedule,Long> {
}
