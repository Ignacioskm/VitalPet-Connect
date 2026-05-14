package com.vitalpet.msappointments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalServiceResponseDTO {
    private Long id;
    private String name;
    private Double price;
}
