package com.smarthealth.repository;

import com.smarthealth.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ContactMessage entity
 * Provides data access methods for contact form submissions
 */
@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    
    /**
     * Find contact messages by email
     * @param email the email address
     * @return list of contact messages from the email
     */
    List<ContactMessage> findByEmail(String email);
    
    /**
     * Find unread contact messages
     * @param isRead the read status
     * @return list of unread contact messages
     */
    List<ContactMessage> findByIsRead(Boolean isRead);
    
    /**
     * Find contact messages by reply status
     * @param isReplied the reply status
     * @return list of contact messages by reply status
     */
    List<ContactMessage> findByIsReplied(Boolean isReplied);
    
    /**
     * Find contact messages within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of contact messages within the date range
     */
    @Query("SELECT cm FROM ContactMessage cm WHERE cm.sentTime BETWEEN :startDate AND :endDate ORDER BY cm.sentTime DESC")
    List<ContactMessage> findBySentTimeBetween(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find recent contact messages (last 7 days)
     * @param date the date threshold
     * @return list of recent contact messages
     */
    @Query("SELECT cm FROM ContactMessage cm WHERE cm.sentTime >= :date ORDER BY cm.sentTime DESC")
    List<ContactMessage> findRecentContactMessages(@Param("date") LocalDateTime date);
    
    /**
     * Find contact messages by subject containing (case insensitive)
     * @param subject the subject to search for
     * @return list of contact messages with matching subjects
     */
    @Query("SELECT cm FROM ContactMessage cm WHERE LOWER(cm.subject) LIKE LOWER(CONCAT('%', :subject, '%'))")
    List<ContactMessage> findBySubjectContainingIgnoreCase(@Param("subject") String subject);
    
    /**
     * Count unread contact messages
     * @param isRead the read status
     * @return count of unread contact messages
     */
    long countByIsRead(Boolean isRead);
    
    /**
     * Count contact messages by reply status
     * @param isReplied the reply status
     * @return count of contact messages by reply status
     */
    long countByIsReplied(Boolean isReplied);
    
    /**
     * Find contact messages requiring attention (unread or unreplied)
     * @return list of contact messages requiring attention
     */
    @Query("SELECT cm FROM ContactMessage cm WHERE cm.isRead = false OR cm.isReplied = false ORDER BY cm.sentTime DESC")
    List<ContactMessage> findMessagesRequiringAttention();
}
