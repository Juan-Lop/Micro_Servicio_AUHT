package com.emocional.auth.dto;

import lombok.Data;

/**
 * DTO para la solicitud de registro.
 */
@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
}
