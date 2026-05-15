package com.vitalpet.mspets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpeciesResponseDTO {
    private Long id;
    private String name;
}
