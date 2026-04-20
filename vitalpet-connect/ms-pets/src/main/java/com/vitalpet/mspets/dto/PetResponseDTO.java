package com.vitalpet.mspets.dto;

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
private boolean active;
private LocalDateTime createdAt;
private String speciesName; //Para devolver solo el nombre y no el objeto entero OjO
private Long ownerId;
}
