package com.smarthealth.repository;

import com.smarthealth.model.SOSLog;
import com.smarthealth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for SOSLog entity
 * Provides data access methods for emergency SOS logs
 */
@Repository
public interface SOSLogRepository extends JpaRepository<SOSLog, Long> {
    
    /**
     * Find SOS logs by user
     * @param user the user
     * @return list of SOS logs for the user
     */
    List<SOSLog> findByUser(User user);
    
    /**
     * Find SOS logs by user ID
     * @param userId the user ID
     * @return list of SOS logs for the user
     */
    List<SOSLog> findByUserId(Long userId);
    
    /**
     * Find SOS logs by status
     * @param status the emergency status
     * @return list of SOS logs with the specified status
     */
    List<SOSLog> findByStatus(SOSLog.EmergencyStatus status);
    
    /**
     * Find SOS logs by emergency type
     * @param emergencyType the emergency type
     * @return list of SOS logs of the specified emergency type
     */
    List<SOSLog> findByEmergencyType(String emergencyType);
    
    /**
     * Find SOS logs within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of SOS logs within the date range
     */
    @Query("SELECT sl FROM SOSLog sl WHERE sl.createdAt BETWEEN :startDate AND :endDate ORDER BY sl.createdAt DESC")
    List<SOSLog> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find recent SOS logs (last 24 hours)
     * @param date the date threshold
     * @return list of recent SOS logs
     */
    @Query("SELECT sl FROM SOSLog sl WHERE sl.createdAt >= :date ORDER BY sl.createdAt DESC")
    List<SOSLog> findRecentSOSLogs(@Param("date") LocalDateTime date);
    
    /**
     * Find pending SOS logs
     * @return list of pending SOS logs
     */
    @Query("SELECT sl FROM SOSLog sl WHERE sl.status = 'PENDING' ORDER BY sl.createdAt ASC")
    List<SOSLog> findPendingSOSLogs();
    
    /**
     * Count SOS logs by status
     * @param status the emergency status
     * @return count of SOS logs with the specified status
     */
    long countByStatus(SOSLog.EmergencyStatus status);
    
    /**
     * Find SOS logs by location containing (case insensitive)
     * @param location the location to search for
     * @return list of SOS logs with matching locations
     */
    @Query("SELECT sl FROM SOSLog sl WHERE LOWER(sl.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<SOSLog> findByLocationContainingIgnoreCase(@Param("location") String location);
    
    /**
     * Find SOS logs by user and status
     * @param userId the user ID
     * @param status the emergency status
     * @return list of SOS logs for the user with the specified status
     */
    List<SOSLog> findByUserIdAndStatus(Long userId, SOSLog.EmergencyStatus status);
}
