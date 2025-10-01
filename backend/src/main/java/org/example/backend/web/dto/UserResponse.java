package org.example.backend.web.dto;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email) {}

