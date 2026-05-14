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
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "La fecha de nacimiento de la mascota debe ser antes o el mismo día del ingreso. No puede estar en el futuro.")
    private LocalDate birthDate;

    @NotNull(message = "El peso de la mascota no puede estar vacía.")
    private double weight;

    @NotNull(message = "Debe especificar el ID de la especie de su mascota.")
    private Long speciesId;

    @NotBlank(message = "El campo de owner ID no puede estar vacío")
    private Long ownerId;
}
