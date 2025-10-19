package com.smarthealth.service;

import com.smarthealth.model.HealthRecord;
import com.smarthealth.model.SOSLog;
import com.smarthealth.model.User;
import com.smarthealth.repository.HealthRecordRepository;
import com.smarthealth.repository.SOSLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SOS Service
 * Handles emergency SOS functionality and health record management
 */
@Service
@Transactional
public class SOSService {

    @Autowired
    private SOSLogRepository sosLogRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private MailService mailService;

    /**
     * Create SOS log
     */
    public SOSLog createSOSLog(User user, String location, String phoneNumber, String emergencyType, String description) {
        SOSLog sosLog = new SOSLog();
        sosLog.setUser(user);
        sosLog.setLocation(location);
        sosLog.setPhoneNumber(phoneNumber);
        sosLog.setEmergencyType(emergencyType);
        sosLog.setDescription(description);
        sosLog.setStatus(SOSLog.EmergencyStatus.PENDING);
        sosLog.setCreatedAt(LocalDateTime.now());

        SOSLog savedLog = sosLogRepository.save(sosLog);

        // Send SOS alert email
        try {
            mailService.sendSOSAlert(user.getEmail(), user.getName(), location, emergencyType);
        } catch (Exception e) {
            // Log error but don't fail the SOS creation
            System.err.println("Failed to send SOS alert email: " + e.getMessage());
        }

        return savedLog;
    }

    /**
     * Get all SOS logs
     */
    public List<SOSLog> getAllSOSLogs() {
        return sosLogRepository.findAll();
    }

    /**
     * Get SOS logs by user
     */
    public List<SOSLog> getSOSLogsByUser(User user) {
        return sosLogRepository.findByUser(user);
    }

    /**
     * Get SOS logs by user ID
     */
    public List<SOSLog> getSOSLogsByUserId(Long userId) {
        return sosLogRepository.findByUserId(userId);
    }

    /**
     * Get pending SOS logs
     */
    public List<SOSLog> getPendingSOSLogs() {
        return sosLogRepository.findPendingSOSLogs();
    }

    /**
     * Get SOS logs by status
     */
    public List<SOSLog> getSOSLogsByStatus(SOSLog.EmergencyStatus status) {
        return sosLogRepository.findByStatus(status);
    }

    /**
     * Update SOS log status
     */
    public SOSLog updateSOSLogStatus(Long sosLogId, SOSLog.EmergencyStatus status) {
        Optional<SOSLog> sosLogOpt = sosLogRepository.findById(sosLogId);
        if (sosLogOpt.isPresent()) {
            SOSLog sosLog = sosLogOpt.get();
            sosLog.setStatus(status);
            
            if (status == SOSLog.EmergencyStatus.RESOLVED) {
                sosLog.setResolvedAt(LocalDateTime.now());
            }
            
            return sosLogRepository.save(sosLog);
        }
        throw new RuntimeException("SOS log not found with ID: " + sosLogId);
    }

    /**
     * Get SOS log by ID
     */
    public Optional<SOSLog> getSOSLogById(Long id) {
        return sosLogRepository.findById(id);
    }

    /**
     * Delete SOS log
     */
    public void deleteSOSLog(Long id) {
        sosLogRepository.deleteById(id);
    }

    /**
     * Get recent SOS logs (last 24 hours)
     */
    public List<SOSLog> getRecentSOSLogs() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        return sosLogRepository.findRecentSOSLogs(yesterday);
    }

    /**
     * Get SOS logs within date range
     */
    public List<SOSLog> getSOSLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return sosLogRepository.findByCreatedAtBetween(startDate, endDate);
    }

    /**
     * Search SOS logs by location
     */
    public List<SOSLog> searchSOSLogsByLocation(String location) {
        return sosLogRepository.findByLocationContainingIgnoreCase(location);
    }

    /**
     * Get SOS statistics
     */
    public long getSOSLogCount() {
        return sosLogRepository.count();
    }

    /**
     * Get SOS count by status
     */
    public long getSOSCountByStatus(SOSLog.EmergencyStatus status) {
        return sosLogRepository.countByStatus(status);
    }

    /**
     * Get SOS count by emergency type
     */
    public long getSOSCountByEmergencyType(String emergencyType) {
        return sosLogRepository.findByEmergencyType(emergencyType).size();
    }

    // Health Record Management Methods

    /**
     * Add health record
     */
    public HealthRecord addHealthRecord(User user, HealthRecord.RecordType type, String value, String notes) {
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setUser(user);
        healthRecord.setType(type);
        healthRecord.setValue(value);
        healthRecord.setNotes(notes);
        healthRecord.setRecordDate(LocalDateTime.now());
        healthRecord.setCreatedAt(LocalDateTime.now());

        return healthRecordRepository.save(healthRecord);
    }

    /**
     * Get health records by user
     */
    public List<HealthRecord> getHealthRecordsByUser(User user) {
        return healthRecordRepository.findByUser(user);
    }

    /**
     * Get health records by user ID
     */
    public List<HealthRecord> getHealthRecordsByUserId(Long userId) {
        return healthRecordRepository.findByUserId(userId);
    }

    /**
     * Get health records by type
     */
    public List<HealthRecord> getHealthRecordsByType(HealthRecord.RecordType type) {
        return healthRecordRepository.findByType(type);
    }

    /**
     * Get health records by user and type
     */
    public List<HealthRecord> getHealthRecordsByUserAndType(User user, HealthRecord.RecordType type) {
        return healthRecordRepository.findByUserAndType(user, type);
    }

    /**
     * Get latest health record by user and type
     */
    public HealthRecord getLatestHealthRecordByUserAndType(Long userId, HealthRecord.RecordType type) {
        return healthRecordRepository.findLatestByUserIdAndType(userId, type);
    }

    /**
     * Get upcoming appointments
     */
    public List<HealthRecord> getUpcomingAppointments(Long userId) {
        return healthRecordRepository.findUpcomingAppointments(userId, LocalDateTime.now());
    }

    /**
     * Update health record
     */
    public HealthRecord updateHealthRecord(Long id, String value, String notes) {
        Optional<HealthRecord> healthRecordOpt = healthRecordRepository.findById(id);
        if (healthRecordOpt.isPresent()) {
            HealthRecord healthRecord = healthRecordOpt.get();
            healthRecord.setValue(value);
            healthRecord.setNotes(notes);
            return healthRecordRepository.save(healthRecord);
        }
        throw new RuntimeException("Health record not found with ID: " + id);
    }

    /**
     * Delete health record
     */
    public void deleteHealthRecord(Long id) {
        healthRecordRepository.deleteById(id);
    }

    /**
     * Get health record by ID
     */
    public Optional<HealthRecord> getHealthRecordById(Long id) {
        return healthRecordRepository.findById(id);
    }

    /**
     * Get health records within date range
     */
    public List<HealthRecord> getHealthRecordsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return healthRecordRepository.findByUserIdAndRecordDateBetween(userId, startDate, endDate);
    }

    /**
     * Count health records by user and type
     */
    public long countHealthRecordsByUserAndType(Long userId, HealthRecord.RecordType type) {
        return healthRecordRepository.countByUserIdAndType(userId, type);
    }
}
