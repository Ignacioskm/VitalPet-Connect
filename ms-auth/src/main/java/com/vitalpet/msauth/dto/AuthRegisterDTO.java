package com.vitalpet.msauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegisterDTO {

    // Datos para el perfil en ms-users
    @NotBlank(message = "El nombre es obligatorio.")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio.")
    private String lastName;

    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "Debe ser un correo electrónico válido.")
    private String email;

    private String phoneNumber;
    private String address;

    @NotBlank(message = "El rol es obligatorio (ej: CLIENT, VET).")
    private String roleName;

    // Dato exclusivo para la credencial en ms-auth
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 6, message = "Por seguridad, la contraseña debe tener al menos 6 caracteres.")
    private String password;
}
