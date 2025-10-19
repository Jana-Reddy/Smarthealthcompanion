package com.smarthealth.controller;

import com.smarthealth.model.User;
import com.smarthealth.service.SOSService;
import com.smarthealth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Dashboard Controller
 * Handles user dashboard and main navigation
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private SOSService sosService;

    /**
     * User dashboard
     */
    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        // Get user's recent health records
        var recentHealthRecords = sosService.getHealthRecordsByUserId(user.getId());
        
        // Get upcoming appointments
        var upcomingAppointments = sosService.getUpcomingAppointments(user.getId());
        
        // Get recent SOS logs
        var recentSOSLogs = sosService.getSOSLogsByUserId(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("recentHealthRecords", recentHealthRecords.stream().limit(5).toList());
        model.addAttribute("upcomingAppointments", upcomingAppointments.stream().limit(3).toList());
        model.addAttribute("recentSOSLogs", recentSOSLogs.stream().limit(3).toList());
        
        return "dashboard";
    }

    /**
     * Profile page
     */
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Update profile
     */
    @PostMapping("/profile")
    public String updateProfile(@RequestParam String name,
                              @RequestParam String email,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        User user = (User) authentication.getPrincipal();
        
        try {
            user.setName(name);
            user.setEmail(email);
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }
        
        return "redirect:/dashboard/profile";
    }

    /**
     * Change password page
     */
    @GetMapping("/change-password")
    public String changePassword(Model model) {
        return "change-password";
    }

    /**
     * Process password change
     */
    @PostMapping("/change-password")
    public String processPasswordChange(@RequestParam String currentPassword,
                                     @RequestParam String newPassword,
                                     @RequestParam String confirmPassword,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        User user = (User) authentication.getPrincipal();
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match.");
            return "redirect:/dashboard/change-password";
        }
        
        try {
            userService.changePassword(user.getId(), newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
        }
        
        return "redirect:/dashboard/change-password";
    }
}
