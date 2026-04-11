package com.vitalpet.msstaff.service;

import com.vitalpet.msstaff.model.Staff;
import com.vitalpet.msstaff.model.StaffSchedule;
import com.vitalpet.msstaff.repository.StaffRepository;
import com.vitalpet.msstaff.repository.StaffScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StaffScheduleService {
    @Autowired
    private StaffScheduleRepository staffSchRepository;

    public List<StaffSchedule> getAll(){return staffSchRepository.findAll();}

    public StaffSchedule getById(Long id){
        return staffSchRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void delete(Long id){
        StaffSchedule staffSchedule = getById(id);
        staffSchRepository.delete(staffSchedule);
    }
}
