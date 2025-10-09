package org.example.domain.repository;

import org.example.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find users by type
    List<User> findByUserType(User.UserType userType);
    
    // Find active users
    List<User> findByIsActiveTrue();
    
    // Find users by name containing (case insensitive)
    List<User> findByNameContainingIgnoreCase(String name);
    
    // Find users by phone number
    Optional<User> findByPhone(String phone);
    
    // Custom query to find available drivers
    @Query("SELECT u FROM User u WHERE u.userType IN ('DRIVER', 'BOTH') AND u.isActive = true")
    List<User> findAvailableDrivers();
    
    // Custom query to find users with email containing
    @Query("SELECT u FROM User u WHERE u.email LIKE %:email%")
    List<User> findByEmailContaining(@Param("email") String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if phone exists
    boolean existsByPhone(String phone);
}

