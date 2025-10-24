package org.ridewithus.controller;

import jakarta.servlet.http.HttpSession;
import org.ridewithus.domain.dto.AuthResponse;
import org.ridewithus.domain.dto.LoginRequest;
import org.ridewithus.domain.dto.RegisterRequest;
import org.ridewithus.domain.dto.UserDTO;
import org.ridewithus.domain.facade.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles UC1 (Register) and UC2 (Login) endpoints
 * Uses Facade pattern to delegate authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}) // Configure CORS for frontend
public class AuthController {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    /**
     * UC1 - Register a new rider account
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authenticationFacade.register(request);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * UC2 - Log in to the system
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        AuthResponse response = authenticationFacade.login(request);

        if (response.isSuccess()) {
            // Store user info in session for role-based routing
            session.setAttribute("userId", response.getUser().getId());
            session.setAttribute("userName", response.getUser().getUserName());
            session.setAttribute("userRole", response.getUser().getRole());
            session.setAttribute("sessionToken", response.getToken());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Logout endpoint
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpSession session) {
        boolean success = authenticationFacade.logout();
        session.invalidate();
        
        return ResponseEntity.ok(new AuthResponse(success, 
            success ? "Logout successful" : "Logout failed", null, null));
    }

    /**
     * Get current logged-in user
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Not authenticated", null, null));
        }

        // Get user details from facade
        UserDTO user = authenticationFacade.getCurrentUser();
        if (user != null) {
            return ResponseEntity.ok(new AuthResponse(true, "Authenticated", user, null));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "User not found", null, null));
        }
    }
}

