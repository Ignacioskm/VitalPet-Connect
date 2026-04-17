package com.vitalpet.msstaff.service;

import com.vitalpet.msstaff.dto.StaffRequestDTO;
import com.vitalpet.msstaff.dto.StaffResponseDTO;
import com.vitalpet.msstaff.model.Specialty;
import com.vitalpet.msstaff.model.Staff;
import com.vitalpet.msstaff.repository.StaffRepository;
import com.vitalpet.msstaff.repository.StaffScheduleRepository;
import com.vitalpet.msstaff.repository.StaffSpecialty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffScheduleRepository staffScheduleRepository;

    @Autowired
    private StaffSpecialty staffSpecialty;


    //Convertir staff a DTO

    private StaffResponseDTO toDTO(Staff staff){
        StaffResponseDTO dto = new StaffResponseDTO();

        dto.setId(staff.getId());
        dto.setFirstName(staff.getFirstName());
        dto.setLastName(staff.getLastName());
        dto.setEmail(staff.getEmail());
        dto.setPhoneNumber(staff.getPhoneNumber());
        dto.setActive(staff.getActive());
        dto.setHireDate(staff.getHireDate());
        dto.setCreatedAt(staff.getCreatedAt());
        dto.setBranchId(staff.getBranchId());
        dto.setSchedules(staff.getSchedules());
        dto.setSpecialtyName(staff.getSpecialty().getName());
        return dto;
    }

    //Traer todos los staff y mapearlos a StaffResponseDTO
    public List<StaffResponseDTO> getAll(){
        return staffRepository.findByActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }


    public StaffResponseDTO getById(Long id){
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new RuntimeException("Staff no encontrado"));
        return toDTO(staff);
    }

    public StaffResponseDTO create(StaffRequestDTO dto){
        if(staffRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("El email ya esta registrado");
        }

        Specialty specialty = staffSpecialty.findByName(dto.getSpecialtyName()).orElseThrow(() -> new RuntimeException("Specialidad no encontrada " + dto.getSpecialtyName()));

        Staff staff = new Staff();
        staff.setFirstName(dto.getFirstName());
        staff.setLastName(dto.getLastName());
        staff.setEmail(dto.getEmail());
        staff.setPhoneNumber(dto.getPhoneNumber());
        staff.setHireDate(dto.getHireDate());

        //Aca traer el id del branch con el nombre! implementar método en branches
        //staff.setBranchId();

        staff.setSpecialty(specialty);

        return toDTO(staffRepository.save(staff));
    }

    public StaffResponseDTO update(Long id, StaffRequestDTO dto){
        Staff existing = staffRepository.findById(id).orElseThrow(() -> new RuntimeException("Staff no encontrado"));

        Specialty specialty = staffSpecialty.findByName(dto.getSpecialtyName()).orElseThrow(() -> new RuntimeException("Specialidad no encontrada " + dto.getSpecialtyName()));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        //No se agrega email! ya que es nuestro atributo inmutable lo que nos permite no tener duplicados
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setHireDate(dto.getHireDate());

        //mismo pedo que arriba
        //existing.setBranchId(dto.getBranchName());

        existing.setSpecialty(specialty);
        return toDTO(staffRepository.save(existing));
    }

    public void desactivate(Long id){
        Staff staff = staffRepository.findById(id).orElseThrow(()-> new RuntimeException("Staff no encontrado"));
        staff.setActive(false);
        staffRepository.save(staff);
    }
}
