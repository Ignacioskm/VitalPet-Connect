package com.vitalpet.msstaff.service;

import com.vitalpet.msstaff.client.BranchClient;
import com.vitalpet.msstaff.client.UserClient;
import com.vitalpet.msstaff.dto.*;
import com.vitalpet.msstaff.exception.ResourceNotFoundException;
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
    @Autowired private StaffRepository staffRepository;
    @Autowired private StaffScheduleRepository staffScheduleRepository;
    @Autowired private StaffSpecialty staffSpecialty;
    @Autowired private BranchClient branchClient;
    @Autowired
    private UserClient userClient;

    //Traer todos los staff y mapearlos a StaffResponseDTO
    public List<StaffResponseDTO> getAll(){
        return staffRepository.findByActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public StaffResponseDTO getById(Long id){
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún miembro del staff el ID: " + id));
        return toDTO(staff);
    }

    public StaffResponseDTO create(StaffRequestDTO dto){
        // Primero validamos al otro microservicio antes de buscar nada más.
        //Con esto nos aseguramos que el id de la sucursal exista y si no mató nomas.
        Boolean branchExists = false;
        try{
            branchExists = branchClient.existsById(dto.getBranchId());
        } catch (Exception e){
            throw new RuntimeException("Error de comunicación al validar la sucursal en ms-branchs");
        }

        //Aca ocupamos el metodo de Boolean para verificar ver si branchExist es False
        if(Boolean.FALSE.equals(branchExists)){
            throw new ResourceNotFoundException("Error: La sucursal con ID" + dto.getBranchId() + "no existe");
        }

        Boolean userExists = false;
        Boolean isVet = false;

        try {
            userExists = userClient.existById(dto.getUserId());
            if(Boolean.TRUE.equals(userExists)){
                isVet = userClient.isVet(dto.getUserId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicacion al validar el usuario en ms users ");
        }

        if(Boolean.FALSE.equals(userExists)){
            throw new ResourceNotFoundException("Error: El usuario con ID " + dto.getUserId() + " no existe.");
        }
        if(Boolean.FALSE.equals(isVet)){
            throw new IllegalArgumentException("Error: El usuario debe tener el rol VET para ser registrado como personal médico.");
        }



        if(staffRepository.existsByEmail(dto.getEmail())){
            throw new IllegalArgumentException("El email ya esta registrado");
        }

        Specialty specialty = staffSpecialty.findByName(dto.getSpecialtyName())
                .orElseThrow(() -> new ResourceNotFoundException("Specialidad no encontrada " + dto.getSpecialtyName()));

        Staff staff = new Staff();
        staff.setFirstName(dto.getFirstName());
        staff.setLastName(dto.getLastName());
        staff.setEmail(dto.getEmail());
        staff.setPhoneNumber(dto.getPhoneNumber());
        staff.setHireDate(dto.getHireDate());
        staff.setBranchId(dto.getBranchId());
        staff.setUserId(dto.getUserId());
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
        Staff existing = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún miembro del staff el ID: " + id));

        Specialty specialty = staffSpecialty.findByName(dto.getSpecialtyName())
                .orElseThrow(() -> new ResourceNotFoundException("Specialidad no encontrada " + dto.getSpecialtyName()));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        //No se agrega email ni el userId! ya que es nuestro atributo inmutable lo que nos permite no tener duplicados
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setHireDate(dto.getHireDate());

        //mismo pedo que arriba
        //existing.setBranchId(dto.getBranchName());

        existing.setSpecialty(specialty);
        return toDTO(staffRepository.save(existing));
    }

    public void desactivate(Long id){
        Staff staff = staffRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No se encontró ningún miembro del staff el ID: " + id));
        staff.setActive(false);
        staffRepository.save(staff);
    }

    //Listamos staff por sede
    public List<StaffResponseDTO> findStaffByBranchId(Long branchId){

        //Primero verificamos que la sede existe
        Boolean branchExists = false;
        try {
            branchExists = branchClient.existsById(branchId);
        } catch (Exception e){
            throw new RuntimeException("Error de comunicación al validar la sucursal.");
        }

        if(Boolean.FALSE.equals(branchExists)){
            throw new ResourceNotFoundException("Error: La sucursal con ID" + branchId + "no existe");
        }

        //Ahora creamos la lógica para buscar a todos los staff que tengan esa branchId
        return staffRepository.findByBranchId(branchId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    //Verificar si existe
    public boolean staffExistsById(Long id){
        return staffRepository.existsById(id);
    }

    //ToDTO Specialty
    public SpecialtyResponseDTO toSpDTO(Specialty specialty){
        SpecialtyResponseDTO dto = new SpecialtyResponseDTO();
        dto.setId(specialty.getId());
        dto.setName(specialty.getName());
        return dto;
    }


    public List<SpecialtyResponseDTO> getAllSpecialties(){
        return staffSpecialty.findAll().stream().map(this::toSpDTO).toList();
    }

    //Metodos AUX
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
        dto.setUserId(staff.getUserId());
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

    //Le pasamos el dto y el staff para pasarlo a entidad y vincular el horario con el staff en la BD
    private StaffSchedule toEntitySchedule(ScheduleRequestDTO sDto, Staff staff){
        //validación para validar integridad del horario.
        if(sDto.getStartTime() != null && sDto.getEndTime() != null
                && sDto.getStartTime().isAfter(sDto.getEndTime())){
            throw new IllegalArgumentException("La hora de inicio no puede ser posterior a la hora termino de la jornada para el día: " + sDto.getDayOfWeek());
        }

        StaffSchedule staffSchedule = new StaffSchedule();
        staffSchedule.setDayOfWeek(sDto.getDayOfWeek());
        staffSchedule.setStartTime(sDto.getStartTime());
        staffSchedule.setEndTime(sDto.getEndTime());
        staffSchedule.setStaff(staff); // <- Aca se vincula el horario con el staff en la BD
        return staffSchedule;
    }

    //Metodo auxiliar para convertirá la entidad de la BD -> en un DTO response
    private ScheduleResponseDTO toScheduleDTO(StaffSchedule entity) {
        ScheduleResponseDTO sDto = new ScheduleResponseDTO();
        sDto.setId(entity.getId());
        sDto.setDayOfWeek(entity.getDayOfWeek());
        sDto.setStartTime(entity.getStartTime());
        sDto.setEndTime(entity.getEndTime());
        return sDto;
    }
}
