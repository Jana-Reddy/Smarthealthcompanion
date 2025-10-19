package com.smarthealth.controller;

import com.smarthealth.model.SOSLog;
import com.smarthealth.model.User;
import com.smarthealth.service.SOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

/**
 * SOS Controller
 * Handles emergency SOS functionality
 */
@Controller
@RequestMapping("/sos")
public class SOSController {

    @Autowired
    private SOSService sosService;

    /**
     * SOS emergency page
     */
    @GetMapping
    public String sosEmergency(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("emergencyTypes", getEmergencyTypes());
        return "sos-emergency";
    }

    /**
     * Process SOS emergency
     */
    @PostMapping("/emergency")
    public String processSOSEmergency(@RequestParam String location,
                                    @RequestParam String phoneNumber,
                                    @RequestParam String emergencyType,
                                    @RequestParam(required = false) String description,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        User user = (User) authentication.getPrincipal();
        
        try {
            sosService.createSOSLog(user, location, phoneNumber, emergencyType, description);
            redirectAttributes.addFlashAttribute("successMessage", 
                "SOS alert sent successfully! Emergency services have been notified.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to send SOS alert: " + e.getMessage());
        }
        
        return "redirect:/sos";
    }

    /**
     * User's SOS logs
     */
    @GetMapping("/logs")
    public String sosLogs(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        List<SOSLog> sosLogs = sosService.getSOSLogsByUserId(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("sosLogs", sosLogs);
        
        return "sos-logs";
    }

    /**
     * SOS log details
     */
    @GetMapping("/log/{id}")
    public String sosLogDetails(@PathVariable Long id, Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        var sosLog = sosService.getSOSLogById(id);
        if (sosLog.isPresent() && sosLog.get().getUser().getId().equals(user.getId())) {
            model.addAttribute("user", user);
            model.addAttribute("sosLog", sosLog.get());
            return "sos-log-details";
        }
        
        return "redirect:/sos/logs";
    }

    /**
     * Emergency contacts page
     */
    @GetMapping("/contacts")
    public String emergencyContacts(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "emergency-contacts";
    }

    /**
     * Emergency procedures page
     */
    @GetMapping("/procedures")
    public String emergencyProcedures(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "emergency-procedures";
    }

    /**
     * Get emergency types
     */
    private List<String> getEmergencyTypes() {
        return Arrays.asList(
            "Medical Emergency",
            "Accident",
            "Fire",
            "Natural Disaster",
            "Crime/Safety",
            "Mental Health Crisis",
            "Drug Overdose",
            "Heart Attack",
            "Stroke",
            "Severe Injury",
            "Other"
        );
    }
}
