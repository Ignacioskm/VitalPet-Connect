package com.vitalpet.msadoptions.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdoptionRequestDTO {

    @NotBlank(message = "El campo de nota de la adopción no puede estar vacío.")
    private String notes;

    @NotBlank(message = "El ID de la mascota es obligatorio.")
    private Long petId;

    @NotBlank(message = "El ID del dueño es obligatorio.")
    private Long userId;

    @NotBlank(message = "El ID del Staff es obligatorio.")
    private Long staffId;

}
