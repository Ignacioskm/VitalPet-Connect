package com.vitalpet.msnotifications.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {

    @NotNull(message = "Debe ingresar un usuario válido.")
    private Long userId;

    @NotBlank(message = "Debe ingresar algún tipo de usuario.")
    private String type;

    @NotBlank(message = "Ingresar un mensaje es obligatorio!.")
    private String message;
}
