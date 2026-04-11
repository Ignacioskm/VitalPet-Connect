package com.vitalpet.msstaff.service;

import com.vitalpet.msstaff.model.Staff;
import com.vitalpet.msstaff.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;

    public List<Staff> getAll(){return staffRepository.findAll();}

    public Staff getById(Long id){
        return staffRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    }

    public void delete(Long id){
        Staff staff = getById(id);
        staffRepository.delete(staff);
    }
}
