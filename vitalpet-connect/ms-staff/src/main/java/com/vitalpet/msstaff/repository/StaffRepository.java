package com.vitalpet.msstaff.repository;

import com.vitalpet.msstaff.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff,Long> {
}
