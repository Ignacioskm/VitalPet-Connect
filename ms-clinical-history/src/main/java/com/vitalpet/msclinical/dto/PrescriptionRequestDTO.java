package com.vitalpet.msclinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionRequestDTO {

    @NotBlank(message = "El nombre del medicamento es obligatorio.")
    private String medicationName;

    @NotBlank(message = "La dosis debe ser especificada.")
    private String dosage;

    @NotNull(message = "Debe especificar los días del tratamiento médico.")
    private Integer durationDays;
}
