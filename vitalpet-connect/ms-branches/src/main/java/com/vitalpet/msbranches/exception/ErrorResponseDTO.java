package com.vitalpet.msbranches.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {

    //Este es el JSON estandarizado que siempre recibirá nuestro frontend.
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<String> details; //En esta lista guardaremos los errores del Jakarta

}
