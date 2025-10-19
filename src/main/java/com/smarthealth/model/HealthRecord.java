package com.smarthealth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * HealthRecord Entity - Represents health tracking data (vitals, appointments, etc.)
 */
@Entity
@Table(name = "health_records")
public class HealthRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType type;
    
    @NotBlank(message = "Value is required")
    @Column(nullable = false)
    private String value;
    
    @Column(length = 500)
    private String notes;
    
    @Column(name = "record_date")
    private LocalDateTime recordDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public HealthRecord() {
        this.createdAt = LocalDateTime.now();
        this.recordDate = LocalDateTime.now();
    }
    
    public HealthRecord(User user, RecordType type, String value, String notes) {
        this();
        this.user = user;
        this.type = type;
        this.value = value;
        this.notes = notes;
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
    
    public RecordType getType() {
        return type;
    }
    
    public void setType(RecordType type) {
        this.type = type;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getRecordDate() {
        return recordDate;
    }
    
    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Enum for Health Record Types
    public enum RecordType {
        BLOOD_PRESSURE("Blood Pressure"),
        HEART_RATE("Heart Rate"),
        TEMPERATURE("Temperature"),
        WEIGHT("Weight"),
        HEIGHT("Height"),
        BLOOD_SUGAR("Blood Sugar"),
        APPOINTMENT("Appointment"),
        MEDICATION("Medication"),
        SYMPTOM("Symptom"),
        OTHER("Other");
        
        private final String displayName;
        
        RecordType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
