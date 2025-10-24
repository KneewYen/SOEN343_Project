package org.ridewithus.domain.facade;

import org.ridewithus.domain.dto.AuthResponse;
import org.ridewithus.domain.dto.LoginRequest;
import org.ridewithus.domain.dto.RegisterRequest;
import org.ridewithus.domain.dto.UserDTO;

public interface AuthenticationFacade {
    /**
     * Register a new user
     * @param request Registration request
     * @return Authentication response with user details
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * Login a user
     * @param request Login request
     * @return Authentication response with user details
     */
    AuthResponse login(LoginRequest request);
    
    /**
     * Get current authenticated user
     * @return User details or null if not authenticated
     */
    UserDTO getCurrentUser();
    
    /**
     * Logout current user
     * @return Success status
     */
    boolean logout();
}
