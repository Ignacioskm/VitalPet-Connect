package com.vitalpet.msbranches.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchesRequestDTO {

    @NotBlank(message = "El nombre de la sucursal es obligatorio.")
    private String name;

    @NotBlank(message = "La dirección de la sucursal no puede estar vacía.")
    private String address;

    @NotBlank(message = "El teléfono de la sucursal no puede estar vacío.")
    private String phone;

    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "Debe ser un correo electrónico válido.")
    private String email;

    @NotBlank(message = "La ciudad no puede quedar en blanco.")
    private String cityName;

}
