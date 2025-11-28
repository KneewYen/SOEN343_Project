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
     * Get user by ID from database (fetches latest data including Flex Dollar balance)
     * @param userId The user ID
     * @return User details or null if not found
     */
    UserDTO getCurrentUserById(Long userId);
    
    /**
     * Logout current user
     */
    void logout();
}