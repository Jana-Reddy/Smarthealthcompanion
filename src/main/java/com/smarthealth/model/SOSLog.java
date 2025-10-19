package com.smarthealth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

/**
 * SOSLog Entity - Represents emergency SOS logs
 */
@Entity
@Table(name = "sos_logs")
public class SOSLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Please provide a valid phone number")
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "emergency_type")
    private String emergencyType;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmergencyStatus status = EmergencyStatus.PENDING;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    // Constructors
    public SOSLog() {
        this.createdAt = LocalDateTime.now();
    }
    
    public SOSLog(User user, String location, String phoneNumber, String emergencyType, String description) {
        this();
        this.user = user;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.emergencyType = emergencyType;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmergencyType() {
        return emergencyType;
    }
    
    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public EmergencyStatus getStatus() {
        return status;
    }
    
    public void setStatus(EmergencyStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    // Enum for Emergency Status
    public enum EmergencyStatus {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        RESOLVED("Resolved"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        EmergencyStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
