package com.vitalpet.msstaff.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Bueno esta clase funciona como un paraguas que va atrapando las distintas excepciones.

    //Primera excepción: 404 -> "No encontrado"
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex){
        //Aca se construye el error.
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError("Not Found");
        error.setMessage(ex.getMessage());
        error.setDetails(null);

        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    //Segunda excepción: 400 -> Faltan datos obligatorios (Los del jakarta)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex){
        //Aquí guardamos los errores
        List<String> errors = new ArrayList<>();

        //Recorremos los errores y los guardamos.
        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()){
            errors.add(fieldError.getDefaultMessage());
        }

        //Construimos el error
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("Validation Error");
        error.setMessage("El formulario contiene errores");
        error.setDetails(errors);

        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    //Tercera excepción: 400 -> Error en el formato JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleMessageNotReadable(HttpMessageNotReadableException ex){
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("Malformed JSON Request");
        error.setMessage("El formato de los datos enviados es incorrecto o hay tipos incompatibles");
        error.setDetails(null);

        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    //Cuarta excepción: 409 -> Choque en la base de datos, datos duplicados etc.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex){

        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setError("Database Conflict");
        error.setMessage("Conflicto con los datos. Es posible que esté intentando guardar un registro duplicado.");
        error.setDetails(null);

        return new ResponseEntity<>(error,HttpStatus.CONFLICT);
    }

    //Quinta excepción: 400 -> Error en las reglas de negocio.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex){
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("Business Rule Violation");
        error.setMessage(ex.getMessage()); //Aca va a ir el mensaje que nosotros pongamos.
        error.setDetails(null);

        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    //Error genérico por si no atrapamos ningún otro.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex){
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setError("Internal Server Error");
        error.setMessage("Ocurrió un error inesperado en el servidor.");
        error.setDetails(null);

        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
