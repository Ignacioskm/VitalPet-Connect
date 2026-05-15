package com.vitalpet.msauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private String firstName;
    private String lastName;

    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "Debe ser un correo electrónico válido.")
    private String email;

    private String phoneNumber;
    private String address;
    private String roleName;
}
