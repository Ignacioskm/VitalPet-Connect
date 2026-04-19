package com.vitalpet.msstaff.dto;

import com.vitalpet.msstaff.model.StaffSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Boolean active = true;
    private LocalDate hireDate;
    private LocalDateTime createdAt;

    //Validar branchID con webclient
    private Long branchId;

    private List<ScheduleResponseDTO> schedules;
    private String specialtyName;


}
