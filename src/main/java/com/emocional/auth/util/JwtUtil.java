package com.emocional.auth.util;

import com.emocional.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidad para la generación de JWT.
 * El Auth Service es el único responsable de CREAR tokens.
 */
@Component
public class JwtUtil {

    // Clave secreta COMPARTIDA con el Diary Service
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    // Token expira en 24 horas (en milisegundos)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; 

    /**
     * Genera un token JWT incluyendo el ID del usuario. 
     * El ID es crucial para que el Diary Service sepa a qué usuario asociar las entradas.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        if (userDetails instanceof User) {
            // Se incluye el ID del usuario en el payload del JWT
            claims.put("userId", ((User) userDetails).getId());
            claims.put("fullName", ((User) userDetails).getName());
        }
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // Email del usuario
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // --- Métodos de validación y extracción (Usados internamente en el login) ---
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    // NOTA: El Auth Service no necesita el método validateToken completo,
    // ya que el token se genera internamente después de la autenticación de credenciales.
}
