package com.vitalpet.msstaff.service;

import com.vitalpet.msstaff.client.BranchClient;
import com.vitalpet.msstaff.dto.ScheduleResponseDTO;
import com.vitalpet.msstaff.dto.StaffRequestDTO;
import com.vitalpet.msstaff.dto.StaffResponseDTO;
import com.vitalpet.msstaff.dto.ScheduleRequestDTO;
import com.vitalpet.msstaff.model.Specialty;
import com.vitalpet.msstaff.model.Staff;
import com.vitalpet.msstaff.model.StaffSchedule;
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

    // Este es el cliente del feign
    @Autowired
    private BranchClient branchClient;


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
        //En vez de pasar los horarios directos mapeamos el DTO de salida
        if (staff.getSchedules() != null) {
            List<ScheduleResponseDTO> scheduleDTOs = staff.getSchedules().stream()
                    .map(this::toScheduleDTO) //
                    .collect(Collectors.toList());
            dto.setSchedules(scheduleDTOs);
        }
        dto.setSpecialtyName(staff.getSpecialty().getName());
        return dto;
    }

    // Metodo auxiliar para los horarios
    //Le pasamos el dto y el staff para pasarlo a entidad y vincular el horario con el staff en la BD
    private StaffSchedule toEntitySchedule(ScheduleRequestDTO sDto, Staff staff){
        StaffSchedule staffSchedule = new StaffSchedule();
        staffSchedule.setDayOfWeek(sDto.getDayOfWeek());
        staffSchedule.setStartTime(sDto.getStartTime());
        staffSchedule.setEndTime(sDto.getEndTime());
        staffSchedule.setStaff(staff); // <- Aca se vincula el horario con el staff en la BD
        return staffSchedule;
    }

    //Metodo auxiliar para convertira la entidad de la BD -> en un DTO response
    private ScheduleResponseDTO toScheduleDTO(StaffSchedule entity) {
        ScheduleResponseDTO sDto = new ScheduleResponseDTO();
        sDto.setId(entity.getId());
        sDto.setDayOfWeek(entity.getDayOfWeek());
        sDto.setStartTime(entity.getStartTime());
        sDto.setEndTime(entity.getEndTime());
        return sDto;
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
        // Primero validamos al otro microservicio antes de buscar nada más.
        //Con esto nos aseguramos que el id de la sucursal exista y si no mató nomas.
        Boolean branchExists = branchClient.existsById(dto.getBranchId());

        //Aca ocupamos el metodo de Boolean para verificar ver si branchExist es False
        if(Boolean.FALSE.equals(branchExists)){
            throw new RuntimeException("Error: La sucursal con ID" + dto.getBranchId() + "no existe");
        }

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
        staff.setBranchId(dto.getBranchId());
        staff.setSpecialty(specialty);

        //Aca verificamos que los horarios estén en el JSON del cliente
        if(dto.getSchedules() != null){

            //Aquí parece redundante a java no le gusta que las variables que se trabajan en un stream
            //cambien tanto, arriba le seteamos parametros, entonces creamos un objeto "Final"
            Staff finalStaff = staff;

            List<StaffSchedule> scheduleEntities = dto.getSchedules()
                    .stream() //<- Aca abrimos la cinta transportadora
                    //Aca llamamos al metodo para cada sDTO pasandole el horario y su dueño
                    .map(sDto -> toEntitySchedule(sDto,finalStaff))
                    //Aca metemos denuevo los objetos ya convertidos en una lista
                    .collect(Collectors.toList());

            //Aca entregamos la lista de entidades al objeto Staff
            staff.setSchedules(scheduleEntities);
        }


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
