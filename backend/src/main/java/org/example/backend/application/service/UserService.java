package org.example.backend.application.service;

import org.example.backend.domain.model.User;
import org.example.backend.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(String name, String email) {
        User u = new User(UUID.randomUUID(), name, email);
        return userRepository.save(u);
    }

    public List<User> list() { return userRepository.findAll(); }
}

