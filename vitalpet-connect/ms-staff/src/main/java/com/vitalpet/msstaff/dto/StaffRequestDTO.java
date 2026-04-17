package com.vitalpet.msstaff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;
    //Se pedira el branchName para ir a buscar el id y verificarlo en el ms de branchs
    private String branchName;
    private String specialtyName;

}
