package org.example.backend.domain.repository;

import org.example.backend.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository port. Infrastructure implementation to be added later.
 */
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
}

