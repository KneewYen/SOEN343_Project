package org.ridewithus.controller;

import jakarta.servlet.http.HttpSession;
import org.ridewithus.domain.dto.AuthResponse;
import org.ridewithus.domain.dto.LoginRequest;
import org.ridewithus.domain.dto.RegisterRequest;
import org.ridewithus.domain.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles UC1 (Register) and UC2 (Login) endpoints
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}) // Configure CORS for frontend
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * UC1 - Register a new rider account
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);

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
        AuthResponse response = authenticationService.login(request);

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
        session.invalidate();
        return ResponseEntity.ok(new AuthResponse(true, "Logout successful", null, null));
    }

    /**
     * Get current logged-in user
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");
        String userRole = (String) session.getAttribute("userRole");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Not authenticated", null, null));
        }

        // Return minimal user info from session
        org.ridewithus.domain.dto.UserDTO userDTO = new org.ridewithus.domain.dto.UserDTO();
        userDTO.setId(userId);
        userDTO.setUserName(userName);
        userDTO.setRole(userRole);

        return ResponseEntity.ok(new AuthResponse(true, "Authenticated", userDTO, null));
    }
}

