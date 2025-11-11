package com.emocional.auth.service;

import com.emocional.auth.dto.AuthResponse;
import com.emocional.auth.dto.LoginRequest;
import com.emocional.auth.dto.RegisterRequest;
import com.emocional.auth.model.User;
import com.emocional.auth.repository.UserRepository;
import com.emocional.auth.util.JwtUtil; // <-- CORRECCIÓN: Usando la clase de utilidad existente
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementación de la lógica de negocio para la autenticación y registro.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // <-- CORRECCIÓN: Inyectando JwtUtil
    private final AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo usuario en la base de datos y genera un token JWT.
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String jwt = jwtUtil.generateToken(user);
        return new AuthResponse(jwt);
    }

    /**
     * Autentica al usuario y genera un token JWT.
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Autenticar usando el AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Si la autenticación es exitosa, recuperar el objeto User para generar el token.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 3. Generar y retornar el JWT
        String jwt = jwtUtil.generateToken(user);
        return new AuthResponse(jwt);
    }
}
