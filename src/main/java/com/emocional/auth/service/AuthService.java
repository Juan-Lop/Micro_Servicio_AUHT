package com.emocional.auth.service;

import com.emocional.auth.dto.AuthResponse;
import com.emocional.auth.dto.LoginRequest;
import com.emocional.auth.dto.RegisterRequest;

/**
 * Interfaz para el servicio de lógica de negocio de autenticación.
 */
public interface AuthService {

    /**
     * Registra un nuevo usuario.
     * @param request DTO con nombre, email y password.
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Inicia sesión de un usuario.
     * @param request DTO con el email y password.
     * @return El token JWT generado.
     */

    /**
     * Inicia sesión de un usuario.
     * @param request DTO con el email y password.
     * 
     */
    AuthResponse login(LoginRequest request);
}
