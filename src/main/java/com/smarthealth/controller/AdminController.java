package com.smarthealth.controller;

import com.smarthealth.model.ContactMessage;
import com.smarthealth.model.SOSLog;
import com.smarthealth.model.User;
import com.smarthealth.service.MailService;
import com.smarthealth.service.SOSService;
import com.smarthealth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Admin Controller
 * Handles admin panel functionality
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SOSService sosService;

    @Autowired
    private MailService mailService;

    /**
     * Admin dashboard
     */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        
        // Get statistics
        long totalUsers = userService.getUserCount();
        long totalAdmins = userService.getAdminCount();
        long totalSOSLogs = sosService.getSOSLogCount();
        long pendingSOSLogs = sosService.getSOSCountByStatus(SOSLog.EmergencyStatus.PENDING);
        long unreadMessages = mailService.getUnreadContactMessages().size();
        
        // Get recent data
        List<User> recentUsers = userService.getUsersWithRecentActivity(7);
        List<SOSLog> recentSOSLogs = sosService.getRecentSOSLogs();
        List<ContactMessage> recentMessages = mailService.getRecentContactMessages(
            java.time.LocalDateTime.now().minusDays(7)
        );
        
        model.addAttribute("admin", admin);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("totalSOSLogs", totalSOSLogs);
        model.addAttribute("pendingSOSLogs", pendingSOSLogs);
        model.addAttribute("unreadMessages", unreadMessages);
        model.addAttribute("recentUsers", recentUsers);
        model.addAttribute("recentSOSLogs", recentSOSLogs);
        model.addAttribute("recentMessages", recentMessages);
        
        return "admin/dashboard";
    }

    /**
     * Users management page
     */
    @GetMapping("/users")
    public String manageUsers(Model model, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        
        List<User> users = userService.getAllUsers();
        
        model.addAttribute("admin", admin);
        model.addAttribute("users", users);
        
        return "admin/users";
    }

    /**
     * Delete user
     */
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        User admin = (User) authentication.getPrincipal();
        
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    /**
     * Toggle user status (ban/unban)
     */
    @PostMapping("/users/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        User admin = (User) authentication.getPrincipal();
        
        try {
            User user = userService.toggleUserStatus(id);
            String status = user.getIsActive() ? "activated" : "banned";
            redirectAttributes.addFlashAttribute("successMessage", 
                "User " + status + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to toggle user status: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    /**
     * SOS logs management page
     */
    @GetMapping("/sos-logs")
    public String manageSOSLogs(Model model, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        
        List<SOSLog> sosLogs = sosService.getAllSOSLogs();
        
        model.addAttribute("admin", admin);
        model.addAttribute("sosLogs", sosLogs);
        
        return "admin/sos-logs";
    }

    /**
     * Update SOS log status
     */
    @PostMapping("/sos-logs/update-status/{id}")
    public String updateSOSLogStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        User admin = (User) authentication.getPrincipal();
        
        try {
            SOSLog.EmergencyStatus emergencyStatus = SOSLog.EmergencyStatus.valueOf(status);
            sosService.updateSOSLogStatus(id, emergencyStatus);
            redirectAttributes.addFlashAttribute("successMessage", "SOS log status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update SOS log status: " + e.getMessage());
        }
        
        return "redirect:/admin/sos-logs";
    }

    /**
     * Delete SOS log
     */
    @PostMapping("/sos-logs/delete/{id}")
    public String deleteSOSLog(@PathVariable Long id,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        User admin = (User) authentication.getPrincipal();
        
        try {
            sosService.deleteSOSLog(id);
            redirectAttributes.addFlashAttribute("successMessage", "SOS log deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete SOS log: " + e.getMessage());
        }
        
        return "redirect:/admin/sos-logs";
    }

    /**
     * Contact messages management page
     */
    @GetMapping("/messages")
    public String manageMessages(Model model, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        
        List<ContactMessage> messages = mailService.getAllContactMessages();
        
        model.addAttribute("admin", admin);
        model.addAttribute("messages", messages);
        
        return "admin/messages";
    }

    /**
     * Mark message as read
     */
    @PostMapping("/messages/mark-read/{id}")
    public String markMessageAsRead(@PathVariable Long id,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        User admin = (User) authentication.getPrincipal();
        
        try {
            mailService.markAsRead(id);
            redirectAttributes.addFlashAttribute("successMessage", "Message marked as read!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to mark message as read: " + e.getMessage());
        }
        
        return "redirect:/admin/messages";
    }

    /**
     * Reply to contact message
     */
    @PostMapping("/messages/reply/{id}")
    public String replyToMessage(@PathVariable Long id,
                               @RequestParam String adminReply,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        User admin = (User) authentication.getPrincipal();
        
        try {
            mailService.replyToContactMessage(id, adminReply);
            redirectAttributes.addFlashAttribute("successMessage", "Reply sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send reply: " + e.getMessage());
        }
        
        return "redirect:/admin/messages";
    }

    /**
     * System settings page
     */
    @GetMapping("/settings")
    public String systemSettings(Model model, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        model.addAttribute("admin", admin);
        return "admin/settings";
    }

    /**
     * Create admin user page
     */
    @GetMapping("/create-admin")
    public String createAdmin(Model model, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        model.addAttribute("admin", admin);
        model.addAttribute("user", new User());
        return "admin/create-admin";
    }

    /**
     * Process create admin
     */
    @PostMapping("/create-admin")
    public String processCreateAdmin(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        User admin = (User) authentication.getPrincipal();
        
        try {
            userService.createAdminUser(name, email, password);
            redirectAttributes.addFlashAttribute("successMessage", "Admin user created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create admin user: " + e.getMessage());
        }
        
        return "redirect:/admin/create-admin";
    }
}
