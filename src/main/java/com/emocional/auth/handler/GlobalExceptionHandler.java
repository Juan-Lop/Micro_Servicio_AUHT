package com.emocional.auth.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.emocional.auth.dto.ErrorResponse;

/**
 * Clase centralizada para el manejo global de excepciones en la aplicación.
 * Asegura que todas las respuestas de error sean uniformes usando el ErrorResponse DTO.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	
	/**
     * Maneja la IllegalArgumentException.
     * Esto se utiliza para errores de registro como "El email ya está en uso" (Bad Request - 400).
     *
     * @param ex La excepción lanzada.
     * @return ResponseEntity con el DTO ErrorResponse y estado HTTP 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                status.value(),
                status.getReasonPhrase()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Maneja la BadCredentialsException, lanzada cuando las credenciales de login son inválidas.
     * Esto se mapea a Unauthorized (401).
     *
     * @param ex La excepción lanzada por Spring Security.
     * @return ResponseEntity con el DTO ErrorResponse y estado HTTP 401.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401
        ErrorResponse errorResponse = new ErrorResponse(
                "Credenciales inválidas. Por favor, verifica tu email y contraseña.",
                status.value(),
                status.getReasonPhrase()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Manejador de excepciones genérico (catch-all) para cualquier excepción no prevista.
     * Esto asegura que nunca se envíe un error no estructurado al cliente (Internal Server Error - 500).
     *
     * @param ex La excepción genérica.
     * @return ResponseEntity con el DTO ErrorResponse y estado HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        ErrorResponse errorResponse = new ErrorResponse(
                "Ocurrió un error inesperado en el servidor.",
                status.value(),
                status.getReasonPhrase()
        );
        // Opcional: registrar la excepción completa aquí para propósitos de debugging.
        // log.error("Error no manejado: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, status);
    }

}
