package org.example.backend.web.controller;

import org.example.backend.application.service.UserService;
import org.example.backend.domain.model.User;
import org.example.backend.web.dto.CreateUserRequest;
import org.example.backend.web.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req) {
        User u = userService.create(req.name(), req.email());
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getName(), u.getEmail()));
    }

    @GetMapping
    public List<UserResponse> list() {
        return userService.list().stream()
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail()))
                .collect(Collectors.toList());
    }
}
