package com.vitalpet.msstaff.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestDTO {

    @NotNull(message = "El día de la semana no puede estar vacío.")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "El comienzo del turno no puede estar vacío.")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull(message = "El término del turno no puede estar vacío.")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
}
