package com.smarthealth.controller;

import com.smarthealth.model.HealthRecord;
import com.smarthealth.model.User;
import com.smarthealth.service.SOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Health Tracker Controller
 * Handles health record management and tracking
 */
@Controller
@RequestMapping("/health-tracker")
public class HealthTrackerController {

    @Autowired
    private SOSService sosService;

    /**
     * Health tracker main page
     */
    @GetMapping
    public String healthTracker(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        var healthRecords = sosService.getHealthRecordsByUserId(user.getId());
        var upcomingAppointments = sosService.getUpcomingAppointments(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("healthRecords", healthRecords);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("recordTypes", Arrays.asList(HealthRecord.RecordType.values()));
        
        return "health-tracker";
    }

    /**
     * Add health record page
     */
    @GetMapping("/add")
    public String addHealthRecord(Model model) {
        model.addAttribute("healthRecord", new HealthRecord());
        model.addAttribute("recordTypes", Arrays.asList(HealthRecord.RecordType.values()));
        return "add-health-record";
    }

    /**
     * Process add health record
     */
    @PostMapping("/add")
    public String processAddHealthRecord(@RequestParam String type,
                                       @RequestParam String value,
                                       @RequestParam(required = false) String notes,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        User user = (User) authentication.getPrincipal();
        
        try {
            HealthRecord.RecordType recordType = HealthRecord.RecordType.valueOf(type);
            sosService.addHealthRecord(user, recordType, value, notes);
            redirectAttributes.addFlashAttribute("successMessage", "Health record added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add health record: " + e.getMessage());
        }
        
        return "redirect:/health-tracker";
    }

    /**
     * Edit health record page
     */
    @GetMapping("/edit/{id}")
    public String editHealthRecord(@PathVariable Long id, Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        var healthRecord = sosService.getHealthRecordById(id);
        if (healthRecord.isPresent() && healthRecord.get().getUser().getId().equals(user.getId())) {
            model.addAttribute("healthRecord", healthRecord.get());
            model.addAttribute("recordTypes", Arrays.asList(HealthRecord.RecordType.values()));
            return "edit-health-record";
        }
        
        return "redirect:/health-tracker";
    }

    /**
     * Process edit health record
     */
    @PostMapping("/edit/{id}")
    public String processEditHealthRecord(@PathVariable Long id,
                                        @RequestParam String value,
                                        @RequestParam(required = false) String notes,
                                        Authentication authentication,
                                        RedirectAttributes redirectAttributes) {
        User user = (User) authentication.getPrincipal();
        
        try {
            var healthRecord = sosService.getHealthRecordById(id);
            if (healthRecord.isPresent() && healthRecord.get().getUser().getId().equals(user.getId())) {
                sosService.updateHealthRecord(id, value, notes);
                redirectAttributes.addFlashAttribute("successMessage", "Health record updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Health record not found or access denied.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update health record: " + e.getMessage());
        }
        
        return "redirect:/health-tracker";
    }

    /**
     * Delete health record
     */
    @PostMapping("/delete/{id}")
    public String deleteHealthRecord(@PathVariable Long id,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        User user = (User) authentication.getPrincipal();
        
        try {
            var healthRecord = sosService.getHealthRecordById(id);
            if (healthRecord.isPresent() && healthRecord.get().getUser().getId().equals(user.getId())) {
                sosService.deleteHealthRecord(id);
                redirectAttributes.addFlashAttribute("successMessage", "Health record deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Health record not found or access denied.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete health record: " + e.getMessage());
        }
        
        return "redirect:/health-tracker";
    }

    /**
     * Health records by type
     */
    @GetMapping("/type/{type}")
    public String healthRecordsByType(@PathVariable String type, Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        try {
            HealthRecord.RecordType recordType = HealthRecord.RecordType.valueOf(type.toUpperCase());
            var healthRecords = sosService.getHealthRecordsByUserAndType(user, recordType);
            
            model.addAttribute("user", user);
            model.addAttribute("healthRecords", healthRecords);
            model.addAttribute("recordType", recordType);
            
        } catch (IllegalArgumentException e) {
            return "redirect:/health-tracker";
        }
        
        return "health-records-by-type";
    }

    /**
     * Health statistics page
     */
    @GetMapping("/statistics")
    public String healthStatistics(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        // Get statistics for different record types
        var bloodPressureCount = sosService.countHealthRecordsByUserAndType(user.getId(), HealthRecord.RecordType.BLOOD_PRESSURE);
        var heartRateCount = sosService.countHealthRecordsByUserAndType(user.getId(), HealthRecord.RecordType.HEART_RATE);
        var temperatureCount = sosService.countHealthRecordsByUserAndType(user.getId(), HealthRecord.RecordType.TEMPERATURE);
        var weightCount = sosService.countHealthRecordsByUserAndType(user.getId(), HealthRecord.RecordType.WEIGHT);
        
        model.addAttribute("user", user);
        model.addAttribute("bloodPressureCount", bloodPressureCount);
        model.addAttribute("heartRateCount", heartRateCount);
        model.addAttribute("temperatureCount", temperatureCount);
        model.addAttribute("weightCount", weightCount);
        
        return "health-statistics";
    }
}
