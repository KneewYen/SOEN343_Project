package org.ridewithus.domain.services.impl;

import org.ridewithus.domain.dto.AuthResponse;
import org.ridewithus.domain.dto.LoginRequest;
import org.ridewithus.domain.dto.RegisterRequest;
import org.ridewithus.domain.dto.UserDTO;
import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.services.AuthenticationService;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of AuthenticationService
 * Handles login and registration business logic
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    
    @Autowired
    private UserRepository userRepository;
    
    private UserDTO currentUser;
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        try {
            // Validate input
            if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
                return new AuthResponse(false, "Full name is required", null, null);
            }
            if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
                return new AuthResponse(false, "Username is required", null, null);
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return new AuthResponse(false, "Email is required", null, null);
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return new AuthResponse(false, "Password must be at least 6 characters", null, null);
            }

            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return new AuthResponse(false, "Email already taken", null, null);
            }

            // Check if username already exists
            if (userRepository.existsByUserName(request.getUserName())) {
                return new AuthResponse(false, "Username already taken", null, null);
            }

            // Hash the password
            String hashedPassword = hashPassword(request.getPassword());

            // Create new user
            User user = new User();
            user.setFullName(request.getFullName());
            user.setUserName(request.getUserName());
            user.setEmail(request.getEmail());
            user.setAddress(request.getAddress());
            user.setPassword(hashedPassword);
            user.setRole("rider"); // Default role

            // Save to database
            User savedUser = userRepository.save(user);

            // Create response DTO (exclude password)
            UserDTO userDTO = mapToUserDTO(savedUser);

            return new AuthResponse(true, "Registration successful", userDTO, null);

        } catch (Exception e) {
            return new AuthResponse(false, "Registration failed: " + e.getMessage(), null, null);
        }
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // Validate input
            if (request.getUsernameOrEmail() == null || request.getUsernameOrEmail().trim().isEmpty()) {
                return new AuthResponse(false, "Username or email is required", null, null);
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return new AuthResponse(false, "Password is required", null, null);
            }

            // Find user by email or username
            Optional<User> userOptional = findUserByUsernameOrEmail(request.getUsernameOrEmail());

            if (userOptional.isEmpty()) {
                return new AuthResponse(false, "Account does not exist", null, null);
            }

            User user = userOptional.get();

            // Verify password
            String hashedInputPassword = hashPassword(request.getPassword());
            if (!hashedInputPassword.equals(user.getPassword())) {
                return new AuthResponse(false, "Incorrect username or password", null, null);
            }

            // Generate session token
            String sessionToken = generateSessionToken();

            // Create response DTO (exclude password)
            UserDTO userDTO = mapToUserDTO(user);
            
            // Store current user
            this.currentUser = userDTO;

            return new AuthResponse(true, "Login successful", userDTO, sessionToken);

        } catch (Exception e) {
            return new AuthResponse(false, "Login failed: " + e.getMessage(), null, null);
        }
    }
    
    @Override
    public UserDTO getCurrentUser() {
        return this.currentUser;
    }
    
    @Override
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Find user by username or email
     */
    private Optional<User> findUserByUsernameOrEmail(String usernameOrEmail) {
        // Try to find by email first
        Optional<User> userByEmail = userRepository.findByEmail(usernameOrEmail);
        if (userByEmail.isPresent()) {
            return userByEmail;
        }
        // Try to find by username
        return userRepository.findByUserName(usernameOrEmail);
    }

    /**
     * Hash password using SHA-256
     * In production, use BCrypt or Argon2
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Generate a simple session token
     * In production, use JWT or Spring Security
     */
    private String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Map User entity to UserDTO (exclude password)
     */
    private UserDTO mapToUserDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getFullName(),
            user.getUserName(),
            user.getEmail(),
            user.getRole(),
            user.getAddress()
        );
    }
}
