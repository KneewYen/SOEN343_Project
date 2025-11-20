package org.ridewithus.infrastructure.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ridewithus.domain.entity.User;

//import java.util.List;  //returns list of all users that follow the criteria 
import java.util.Optional;  // returns Optional.of(user) if found. To get user, do Optional.get(). If no match : Optional.empty()
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

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

    /**
     * Atomically add flex dollars to a user's balance.
     * Returns number of rows updated (should be 1 for success).
     */
    @Modifying
    @Transactional
    @Query("update User u set u.flex_dollar_balance = u.flex_dollar_balance + :amount where u.id = :userId")
    int incrementFlexDollarsById(@Param("userId") Long userId, @Param("amount") int amount);

    /**
     * Atomically apply (deduct) flex dollars from a user's balance, never going below zero.
     * Uses a native query to perform a CASE expression and floor at zero.
     */
    @Modifying
    @Transactional
    @Query(value = "update users set flex_  dollar_balance = case when flex_dollar_balance >= :amount then flexdollarbalance - :amount else 0 end where id = :userId", nativeQuery = true)
    int applyFlexDollarsById(@Param("userId") Long userId, @Param("amount") int amount);

    /**
     * Lock the user row for update (pessimistic) when performing multi-step operations.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :userId")
    Optional<User> findByIdForUpdate(@Param("userId") Long userId);

}
