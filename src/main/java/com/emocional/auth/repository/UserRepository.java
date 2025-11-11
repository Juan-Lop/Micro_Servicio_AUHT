package com.emocional.auth.repository;

import com.emocional.auth.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad User.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
