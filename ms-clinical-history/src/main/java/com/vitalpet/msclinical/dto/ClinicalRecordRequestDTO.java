package com.vitalpet.msclinical.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalRecordRequestDTO {

    @NotNull(message = "El ID de la mascota es obligatorio.")
    private Long petId;

    @NotNull(message = "El ID del staff es obligatorio.")
    private Long staffId;

    @NotBlank(message = "La razón de la consulta no puede estar vacía.")
    private String reason;

    @NotBlank(message = "El diagnóstico es obligatorio.")
    private String diagnosis;

    @NotBlank(message = "El tratamiento es obligatorio.")
    private String treatment;

    @NotBlank(message = "Las notas no pueden estar vacías.")
    private String notes;

    @Valid //Esto es para que entre a validar las prescriptions
    private List<PrescriptionRequestDTO> prescriptions;
}
