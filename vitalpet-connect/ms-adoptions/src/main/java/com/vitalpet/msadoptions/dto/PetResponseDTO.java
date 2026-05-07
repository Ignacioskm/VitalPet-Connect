package com.vitalpet.msadoptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetResponseDTO {

    private Long id;
    private String name;
    private String breed;
    private LocalDate birthDate;
    private double weight;
    private Boolean active;
    private LocalDateTime createdAt;
    private Long speciesId;
    private Long ownerId;

}
