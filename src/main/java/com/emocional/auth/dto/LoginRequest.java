package com.emocional.auth.dto;

import lombok.Data;

/**
 * DTO para la solicitud de inicio de sesión.
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
}
