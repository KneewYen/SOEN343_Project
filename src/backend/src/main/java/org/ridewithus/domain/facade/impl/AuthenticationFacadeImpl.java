package org.ridewithus.domain.facade.impl;

import org.ridewithus.domain.dto.AuthResponse;
import org.ridewithus.domain.dto.LoginRequest;
import org.ridewithus.domain.dto.RegisterRequest;
import org.ridewithus.domain.dto.UserDTO;
import org.ridewithus.domain.facade.AuthenticationFacade;
import org.ridewithus.domain.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        try {
            // Delegate to authentication service
            return authenticationService.register(request);
        } catch (Exception e) {
            return AuthResponse.builder()
                .success(false)
                .message("Registration failed: " + e.getMessage())
                .build();
        }
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // Delegate to authentication service
            return authenticationService.login(request);
        } catch (Exception e) {
            return AuthResponse.builder()
                .success(false)
                .message("Login failed: " + e.getMessage())
                .build();
        }
    }
    
    @Override
    public UserDTO getCurrentUser() {
        try {
            // Delegate to authentication service
            return authenticationService.getCurrentUser();
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public boolean logout() {
        try {
            // Delegate to authentication service
            authenticationService.logout();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
