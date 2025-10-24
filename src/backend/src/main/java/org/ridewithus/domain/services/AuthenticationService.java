package org.ridewithus.domain.services;

import org.ridewithus.domain.dto.AuthResponse;
import org.ridewithus.domain.dto.LoginRequest;
import org.ridewithus.domain.dto.RegisterRequest;
import org.ridewithus.domain.dto.UserDTO;

public interface AuthenticationService {
    /**
     * Register a new user
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * Login a user
     */
    AuthResponse login(LoginRequest request);
    
    /**
     * Get current authenticated user
     */
    UserDTO getCurrentUser();
    
    /**
     * Logout current user
     */
    void logout();
}