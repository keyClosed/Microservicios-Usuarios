package com.plaza.usuarios_service.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

// Esta clase manejará las excepciones globalmente en toda la aplicación
@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de excepciones específicas como IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        // Retorna un mensaje de error 400 (Bad Request) con el mensaje de la excepción
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Manejo de excepciones generales (cualquier otra excepción no controlada)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        // Retorna un mensaje de error 500 (Internal Server Error) con el mensaje de la excepción
        return new ResponseEntity<>("Error interno del servidor: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String mensaje = fieldError != null ? fieldError.getDefaultMessage() : "Error de validación";
        return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
    }
}