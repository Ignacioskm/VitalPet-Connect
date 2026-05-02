package com.vitalpet.msnotifications.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {
    private Long userId;
    private String type;
    private String message;
}
