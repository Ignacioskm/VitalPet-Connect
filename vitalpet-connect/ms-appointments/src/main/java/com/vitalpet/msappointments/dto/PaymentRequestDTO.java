package com.vitalpet.msappointments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
    private Double amount;
    private Long userId;
    private Long appointmentId;
}
