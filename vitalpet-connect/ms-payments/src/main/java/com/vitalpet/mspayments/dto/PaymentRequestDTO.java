package com.vitalpet.mspayments.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {

    @PositiveOrZero(message = "Debe ingresar una cantidad positiva")
    private Double amount;

    @NotNull(message = "Debe ingresar un usuario válido.")
    private Long userId;

    @NotNull(message = "Debe ingresar una cita válida.")
    private Long appointmentId;
}
