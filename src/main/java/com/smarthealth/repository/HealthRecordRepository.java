package com.smarthealth.repository;

import com.smarthealth.model.HealthRecord;
import com.smarthealth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for HealthRecord entity
 * Provides data access methods for health tracking
 */
@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    
    /**
     * Find health records by user
     * @param user the user
     * @return list of health records for the user
     */
    List<HealthRecord> findByUser(User user);
    
    /**
     * Find health records by user ID
     * @param userId the user ID
     * @return list of health records for the user
     */
    List<HealthRecord> findByUserId(Long userId);
    
    /**
     * Find health records by type
     * @param type the record type
     * @return list of health records of the specified type
     */
    List<HealthRecord> findByType(HealthRecord.RecordType type);
    
    /**
     * Find health records by user and type
     * @param user the user
     * @param type the record type
     * @return list of health records for the user of the specified type
     */
    List<HealthRecord> findByUserAndType(User user, HealthRecord.RecordType type);
    
    /**
     * Find health records by user ID and type
     * @param userId the user ID
     * @param type the record type
     * @return list of health records for the user of the specified type
     */
    List<HealthRecord> findByUserIdAndType(Long userId, HealthRecord.RecordType type);
    
    /**
     * Find health records within a date range
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of health records within the date range
     */
    @Query("SELECT hr FROM HealthRecord hr WHERE hr.user.id = :userId AND hr.recordDate BETWEEN :startDate AND :endDate ORDER BY hr.recordDate DESC")
    List<HealthRecord> findByUserIdAndRecordDateBetween(@Param("userId") Long userId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find latest health record by user and type
     * @param userId the user ID
     * @param type the record type
     * @return the latest health record of the specified type for the user
     */
    @Query("SELECT hr FROM HealthRecord hr WHERE hr.user.id = :userId AND hr.type = :type ORDER BY hr.recordDate DESC LIMIT 1")
    HealthRecord findLatestByUserIdAndType(@Param("userId") Long userId, @Param("type") HealthRecord.RecordType type);
    
    /**
     * Count health records by user and type
     * @param userId the user ID
     * @param type the record type
     * @return count of health records of the specified type for the user
     */
    long countByUserIdAndType(Long userId, HealthRecord.RecordType type);
    
    /**
     * Find upcoming appointments
     * @param userId the user ID
     * @param currentDate the current date
     * @return list of upcoming appointments
     */
    @Query("SELECT hr FROM HealthRecord hr WHERE hr.user.id = :userId AND hr.type = 'APPOINTMENT' AND hr.recordDate > :currentDate ORDER BY hr.recordDate ASC")
    List<HealthRecord> findUpcomingAppointments(@Param("userId") Long userId, @Param("currentDate") LocalDateTime currentDate);
}
