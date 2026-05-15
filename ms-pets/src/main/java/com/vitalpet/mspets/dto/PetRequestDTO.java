package com.vitalpet.mspets.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetRequestDTO {

    @NotBlank(message = "El nombre de la mascota no puede estar vacío.")
    private String name;

    @NotBlank(message = "La especie de la mascota no puede estar vacía.")
    private String breed;

    @NotNull(message = "La fecha de nacimiento no puede estar vacía.")
    private LocalDate birthDate;

    @NotNull(message = "El peso de la mascota no puede estar vacía.")
    private double weight;

    @NotNull(message = "Debe especificar el ID de la especie de su mascota.")
    private Long speciesId;

    private Long ownerId;
}
