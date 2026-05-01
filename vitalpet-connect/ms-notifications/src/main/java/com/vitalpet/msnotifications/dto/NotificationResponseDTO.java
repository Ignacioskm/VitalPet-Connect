package com.vitalpet.msnotifications.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {
    private Long id;

    private String message;
    private Boolean readFlag;
    private LocalDate sentAt;

    private Long typeId;
    private Long userId;
}
