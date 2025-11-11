package com.emocional.auth.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO estandarizado para todas las respuestas de error de la API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    // El mensaje de error específico para el usuario
    private String message;
    
    // El código de estado HTTP (ej: 400)
    private int status;
    
    // La frase de razón del estado (ej: Bad Request)
    private String error;

    // Timestamp del momento en que ocurrió el error
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String message, int status, String error) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }
}