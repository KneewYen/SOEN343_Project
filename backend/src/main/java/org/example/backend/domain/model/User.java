package org.example.backend.domain.model;

import java.util.UUID;

/**
 * Simple domain model placeholder. Extend with invariants later.
 */
public class User {
    private final UUID id;
    private String name;
    private String email;

    public User(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}

