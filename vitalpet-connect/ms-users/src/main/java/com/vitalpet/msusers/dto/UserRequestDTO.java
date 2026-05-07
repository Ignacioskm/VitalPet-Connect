package com.vitalpet.msusers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío.")
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío.")
    private String lastName;

    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "Debe ser un correo electrónico válido.")
    private String email;

    @NotBlank(message = "Debe ingresar un número de teléfono.")
    private String phoneNumber;

    @NotBlank(message = "La dirección no puede estar vacía.")
    private String address;

    @NotBlank(message = "Debe tener un rol de usuario: CLIENT, VET, RECEPTIONIST, ADMIN.")
    private String roleName;
}
