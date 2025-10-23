package org.ridewithus.infrastructure.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ridewithus.domain.entity.User;

//import java.util.List;  //returns list of all users that follow the criteria 
import java.util.Optional;  // returns Optional.of(user) if found. To get user, do Optional.get(). If no match : Optional.empty()

@Repository
public interface  UserRepository extends JpaRepository<User, Long> {
    // Find user by email
    Optional<User> findByEmail(String email);

    // Find user by username
    Optional<User> findByUserName(String userName);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if username exists
    boolean existsByUserName(String userName);
}
