package com.smarthealth.repository;

import com.smarthealth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * Provides data access methods for user management
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email address
     * @param email the email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     * @param email the email address
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by role
     * @param role the user role
     * @return list of users with the specified role
     */
    List<User> findByRole(User.Role role);
    
    /**
     * Find active users
     * @param isActive the active status
     * @return list of active/inactive users
     */
    List<User> findByIsActive(Boolean isActive);
    
    /**
     * Find users created after a specific date
     * @param date the creation date
     * @return list of users created after the date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find users by name containing (case insensitive)
     * @param name the name to search for
     * @return list of users with matching names
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Count users by role
     * @param role the user role
     * @return count of users with the specified role
     */
    long countByRole(User.Role role);
    
    /**
     * Find users with recent login activity
     * @param date the date threshold
     * @return list of users with recent login activity
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin >= :date")
    List<User> findUsersWithRecentActivity(@Param("date") LocalDateTime date);
}
