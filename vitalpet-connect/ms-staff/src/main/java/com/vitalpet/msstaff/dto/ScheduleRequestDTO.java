package com.vitalpet.msstaff.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @NotNull(message = "Debe ingresar un día de la semana.")
    private DayOfWeek dayOfWeek;

    //Revisar si debe llevar formato/pattern
    @NotNull(message = "Debe ingresar el inicio del turno.")
    private LocalTime startTime;

    @NotNull(message = "Debe ingresar el fin del turno.")
    private LocalTime endTime;
}
