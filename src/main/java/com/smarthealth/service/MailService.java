package com.smarthealth.service;

import com.smarthealth.model.ContactMessage;
import com.smarthealth.model.HealthRecord;
import com.smarthealth.model.User;
import com.smarthealth.repository.ContactMessageRepository;
import com.smarthealth.repository.HealthRecordRepository;
import com.smarthealth.repository.SOSLogRepository;
import com.smarthealth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Mail Service
 * Handles email sending functionality for contact form and notifications
 */
@Service
@Transactional
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name}")
    private String appName;

    /**
     * Send contact form email
     */
    public void sendContactFormEmail(ContactMessage contactMessage) {
        try {
            // Save contact message to database
            contactMessageRepository.save(contactMessage);

            // Send email to admin
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(fromEmail); // Send to admin email
            message.setSubject("New Contact Form Submission - " + contactMessage.getSubject());
            message.setText(buildContactFormEmailBody(contactMessage));
            
            mailSender.send(message);

            // Send confirmation email to user
            sendConfirmationEmail(contactMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send contact form email: " + e.getMessage());
        }
    }

    /**
     * Send confirmation email to user
     */
    private void sendConfirmationEmail(ContactMessage contactMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(contactMessage.getEmail());
        message.setSubject("Thank you for contacting " + appName);
        message.setText(buildConfirmationEmailBody(contactMessage));
        
        mailSender.send(message);
    }

    /**
     * Send SOS alert email
     */
    public void sendSOSAlert(String userEmail, String userName, String location, String emergencyType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(fromEmail); // Send to admin email
            message.setSubject("🚨 SOS EMERGENCY ALERT - " + emergencyType);
            message.setText(buildSOSAlertEmailBody(userEmail, userName, location, emergencyType));
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SOS alert email: " + e.getMessage());
        }
    }

    /**
     * Send appointment reminder email
     */
    public void sendAppointmentReminder(String userEmail, String userName, HealthRecord appointment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(userEmail);
            message.setSubject("Appointment Reminder - " + appName);
            message.setText(buildAppointmentReminderBody(userName, appointment));
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send appointment reminder: " + e.getMessage());
        }
    }

    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(String userEmail, String userName, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(userEmail);
            message.setSubject("Password Reset - " + appName);
            message.setText(buildPasswordResetEmailBody(userName, resetToken));
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    /**
     * Build contact form email body
     */
    private String buildContactFormEmailBody(ContactMessage contactMessage) {
        return String.format("""
            New Contact Form Submission
            
            Name: %s
            Email: %s
            Subject: %s
            Message: %s
            Sent Time: %s
            
            Please respond to this inquiry as soon as possible.
            
            Best regards,
            %s System
            """, 
            contactMessage.getName(),
            contactMessage.getEmail(),
            contactMessage.getSubject(),
            contactMessage.getMessage(),
            contactMessage.getSentTime(),
            appName
        );
    }

    /**
     * Build confirmation email body
     */
    private String buildConfirmationEmailBody(ContactMessage contactMessage) {
        return String.format("""
            Dear %s,
            
            Thank you for contacting %s. We have received your message regarding "%s" and will get back to you as soon as possible.
            
            Your message:
            %s
            
            If you have any urgent health concerns, please contact emergency services immediately.
            
            Best regards,
            %s Team
            """, 
            contactMessage.getName(),
            appName,
            contactMessage.getSubject(),
            contactMessage.getMessage(),
            appName
        );
    }

    /**
     * Build SOS alert email body
     */
    private String buildSOSAlertEmailBody(String userEmail, String userName, String location, String emergencyType) {
        return String.format("""
            🚨 EMERGENCY SOS ALERT 🚨
            
            User Details:
            Name: %s
            Email: %s
            Location: %s
            Emergency Type: %s
            Time: %s
            
            This is an automated alert from the Smart Health Companion system.
            Please take immediate action if this is a genuine emergency.
            
            System Alert
            """, 
            userName,
            userEmail,
            location,
            emergencyType,
            LocalDateTime.now()
        );
    }

    /**
     * Build appointment reminder email body
     */
    private String buildAppointmentReminderBody(String userName, HealthRecord appointment) {
        return String.format("""
            Dear %s,
            
            This is a reminder for your upcoming appointment:
            
            Date: %s
            Details: %s
            Notes: %s
            
            Please arrive 15 minutes early for your appointment.
            
            Best regards,
            %s Team
            """, 
            userName,
            appointment.getRecordDate(),
            appointment.getValue(),
            appointment.getNotes() != null ? appointment.getNotes() : "No additional notes",
            appName
        );
    }

    /**
     * Build password reset email body
     */
    private String buildPasswordResetEmailBody(String userName, String resetToken) {
        return String.format("""
            Dear %s,
            
            You have requested a password reset for your %s account.
            
            To reset your password, please click the following link:
            http://localhost:8080/reset-password?token=%s
            
            This link will expire in 24 hours.
            
            If you did not request this password reset, please ignore this email.
            
            Best regards,
            %s Team
            """, 
            userName,
            appName,
            resetToken,
            appName
        );
    }

    /**
     * Get all contact messages
     */
    public List<ContactMessage> getAllContactMessages() {
        return contactMessageRepository.findAll();
    }

    /**
     * Get unread contact messages
     */
    public List<ContactMessage> getUnreadContactMessages() {
        return contactMessageRepository.findByIsRead(false);
    }

    /**
     * Mark contact message as read
     */
    public void markAsRead(Long messageId) {
        Optional<ContactMessage> messageOpt = contactMessageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            ContactMessage message = messageOpt.get();
            message.setIsRead(true);
            contactMessageRepository.save(message);
        }
    }

    /**
     * Reply to contact message
     */
    public void replyToContactMessage(Long messageId, String adminReply) {
        Optional<ContactMessage> messageOpt = contactMessageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            ContactMessage message = messageOpt.get();
            message.setAdminReply(adminReply);
            message.setIsReplied(true);
            message.setReplyTime(LocalDateTime.now());
            contactMessageRepository.save(message);

            // Send reply email to user
            sendReplyEmail(message);
        }
    }

    /**
     * Send reply email to user
     */
    private void sendReplyEmail(ContactMessage contactMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(contactMessage.getEmail());
        message.setSubject("Re: " + contactMessage.getSubject());
        message.setText(buildReplyEmailBody(contactMessage));
        
        mailSender.send(message);
    }

    /**
     * Build reply email body
     */
    private String buildReplyEmailBody(ContactMessage contactMessage) {
        return String.format("""
            Dear %s,
            
            Thank you for your message regarding "%s".
            
            Our response:
            %s
            
            If you have any further questions, please don't hesitate to contact us.
            
            Best regards,
            %s Team
            """, 
            contactMessage.getName(),
            contactMessage.getSubject(),
            contactMessage.getAdminReply(),
            appName
        );
    }
    public List<ContactMessage> getRecentContactMessages(LocalDateTime fromDate) {
        return contactMessageRepository.findRecentContactMessages(fromDate);
    }

}
