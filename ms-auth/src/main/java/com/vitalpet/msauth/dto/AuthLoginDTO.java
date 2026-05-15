package com.vitalpet.msauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthLoginDTO {

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe tener un formato de email válido.")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}
