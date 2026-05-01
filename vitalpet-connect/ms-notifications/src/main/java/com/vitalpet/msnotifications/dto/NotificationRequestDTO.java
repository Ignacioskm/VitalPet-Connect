package com.vitalpet.msnotifications.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {
    private String message;
    private Boolean readFlag;
    private LocalDate sentAt;
}
