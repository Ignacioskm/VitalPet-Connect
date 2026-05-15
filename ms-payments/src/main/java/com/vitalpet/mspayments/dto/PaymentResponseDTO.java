package com.vitalpet.mspayments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {

    private Long id;
    private Double amount;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private String statusName;
    private String methodName;
    private Long userId;
    private Long appointmentId;
}
