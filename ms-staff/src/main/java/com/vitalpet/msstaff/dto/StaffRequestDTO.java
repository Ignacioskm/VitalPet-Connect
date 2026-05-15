package com.vitalpet.msstaff.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío.")
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío.")
    private String lastName;

    @NotBlank(message = "Debe ingresar un correo electrónico.")
    @Email(message = "Debe ingresar un correo electrónico con formato válido.")
    private String email;

    @NotBlank(message = "Debe ingresar un número telefónico.")
    private String phoneNumber;

    @NotNull(message = "Debe ingresar la fecha de contratación.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "La fecha de contratación no puede ser posterior a la fecha de hoy.")
    private LocalDate hireDate;

    //Se pide el ID para verificar si la ID funciona (en el front esto sería una lista despegable)

    @NotNull(message = "Debe ingresar el ID de la sucursal donde trabaja.")
    private Long branchId;

    @NotBlank(message = "Debe especificar el nombre de la especialidad.")
    private String specialtyName;

    private List<ScheduleRequestDTO> schedules; // <-- Horarios de nuestro Staff

}
