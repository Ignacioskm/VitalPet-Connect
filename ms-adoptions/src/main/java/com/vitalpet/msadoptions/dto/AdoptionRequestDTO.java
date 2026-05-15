package com.vitalpet.msadoptions.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdoptionRequestDTO {

    @NotBlank(message = "El campo de nota de la adopción no puede estar vacío.")
    private String notes;

    @NotNull(message = "El ID de la mascota es obligatorio.")
    private Long petId;

    @NotNull(message = "El ID del dueño es obligatorio.")
    private Long userId;

}
