package org.example.backend.infrastructure.persistence;

import org.example.backend.domain.model.User;
import org.example.backend.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Temporary in-memory repository. Replace with JPA implementation later.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<UUID, User> store = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<User> findAll() { return new ArrayList<>(store.values()); }
}
