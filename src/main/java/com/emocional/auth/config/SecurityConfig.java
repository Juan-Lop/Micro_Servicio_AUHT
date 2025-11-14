package com.emocional.auth.config;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


/**
 * Configuración central de Spring Security para el Auth Service.
 * NOTA: Al ser el Auth Service, no necesitamos un filtro JWT, ya que el único punto
 * de interacción con credenciales es en /auth/login, donde se usa el AuthenticationManager.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final UserDetailsService userDetailsService; // CustomUserDetailsService

	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	/**
     * Define el AuthenticationProvider usando el UserDetailsService y el PasswordEncoder.
     * Esta es la pieza que faltaba para que el proceso de login funcione.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * El AuthenticationManager se usa en el controlador de Auth para ejecutar el login.
     * Ahora utilizará el AuthenticationProvider que definimos.
     */
	
	 @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
	        return authConfig.getAuthenticationManager();
	    }

   

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
             // *** ¡PASO CRÍTICO! Enlaza el AuthenticationProvider al HttpSecurity ***
                // Esto asegura que la configuración de autenticación es utilizada por el filtro.
                .authenticationProvider(authenticationProvider()) 
                
            	// 3. Configurar Autorización de las Peticiones
                .authorizeHttpRequests(auth -> {
                    auth
                        // Permitir acceso público a Login, Register y Swagger UI
                        .requestMatchers("/api/v1/auth/**",
                                         "/v3/api-docs/**",
                                         "/swagger-ui/**",
                                         "/swagger-ui.html").permitAll()
                        
                        // Asegurar todas las demás rutas
                        .anyRequest().authenticated();
                })
                
                // 4. Deshabilitar cabeceras de caché (opcional, pero buena práctica)
                .headers(headers -> headers.cacheControl(HeadersConfigurer.CacheControlConfig::disable))
                // Usamos STATELESS aunque no haya JWT filter, para ser explícitos
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * Define la fuente de configuración de CORS.
     * Retorna CorsConfigurationSource para que Spring Security la use directamente.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
     // FIX CLAVE: Especificar el origen del frontend.
        configuration.setAllowedOrigins(Arrays.asList(
        		"http://localhost:5173",
        		"http://localhost:5174",
        		"http://localhost:3000",
        		"http://localhost:8081",
        		"https://front-end-project-diario.vercel.app",
        		"https://front-end-proyect-diario-djc3xwxo3-juan-lops-projects.vercel.app"
        		));
        
        
        
//        configuration.addAllowedOrigin("*");
        // Define métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Define encabezados permitidos (CRUCIAL para 'Authorization')
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Exponer headers que el frontend puede leer
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Es importante establecer esto como true si se van a usar cookies o encabezados Authorization
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    } 
        
        
        

      
      
        
       
    }

