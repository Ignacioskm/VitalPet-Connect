package com.vitalpet.msstaff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;
    //Se pide el ID para verificar si la ID funciona (en el front esto sería una lista despegable)
    private Long branchId;
    private String specialtyName;
    private List<ScheduleRequestDTO> schedules; // <-- Horarios de nuestro Staff

}
