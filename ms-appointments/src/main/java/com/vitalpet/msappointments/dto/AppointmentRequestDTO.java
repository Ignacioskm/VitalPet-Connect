package com.vitalpet.msappointments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDTO {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") //Esto se lo pregunté a la IA para menejar la fecha así: "2026-05-05T15:30:00"
    private LocalDateTime scheduledAt;

    @NotNull(message = "El id del servicio médico no puede estar vacío")
    private Long medicalServiceId;

    @NotBlank(message = "El campo de notas no puede estar vacio")
    private String notes;

    @NotNull(message = "EL id de mascota no puede estar vacía")
    private Long petId;

    @NotNull(message = "El id de staff no puede estar vacío.")
    private Long staffId;

    @NotNull(message = "El id de la sucursal no puede estar vacía.")
    private Long branchId;
}
