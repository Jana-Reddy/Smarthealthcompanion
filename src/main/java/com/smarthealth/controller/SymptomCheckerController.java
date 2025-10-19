package com.smarthealth.controller;

import com.smarthealth.model.User;
import com.smarthealth.service.HealthDataService;
import com.smarthealth.service.SOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Symptom Checker Controller
 * Handles symptom checking functionality
 */
@Controller
@RequestMapping("/symptom-checker")
public class SymptomCheckerController {

    @Autowired
    private HealthDataService healthDataService;

    @Autowired
    private SOSService sosService;

    /**
     * Symptom checker main page
     */
    @GetMapping
    public String symptomChecker(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "symptom-checker";
    }

    /**
     * Process symptom check
     */
    @PostMapping("/check")
    public String processSymptomCheck(@RequestParam List<String> symptoms,
                                    Model model,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        User user = (User) authentication.getPrincipal();
        
        if (symptoms.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one symptom.");
            return "redirect:/symptom-checker";
        }
        
        try {
            Map<String, Object> result = healthDataService.checkSymptoms(symptoms);
            
            // Save symptoms as health record
            String symptomsText = String.join(", ", symptoms);
            sosService.addHealthRecord(user, 
                com.smarthealth.model.HealthRecord.RecordType.SYMPTOM, 
                symptomsText, 
                "Symptom checker analysis");
            
            model.addAttribute("user", user);
            model.addAttribute("result", result);
            model.addAttribute("symptoms", symptoms);
            
            return "symptom-results";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to analyze symptoms: " + e.getMessage());
            return "redirect:/symptom-checker";
        }
    }

    /**
     * Symptom results page
     */
    @GetMapping("/results")
    public String symptomResults(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "symptom-results";
    }

    /**
     * Get available symptoms (AJAX endpoint)
     */
    @GetMapping("/symptoms")
    @ResponseBody
    public List<String> getAvailableSymptoms() {
        // Return a list of common symptoms
        return Arrays.asList(
            "Fever", "Headache", "Cough", "Sore throat", "Runny nose", "Nausea", "Vomiting",
            "Diarrhea", "Constipation", "Abdominal pain", "Chest pain", "Shortness of breath",
            "Dizziness", "Fatigue", "Muscle aches", "Joint pain", "Rash", "Itching",
            "Swelling", "Weight loss", "Weight gain", "Loss of appetite", "Insomnia",
            "Anxiety", "Depression", "Memory problems", "Confusion", "Seizures",
            "Numbness", "Tingling", "Vision problems", "Hearing problems", "Back pain",
            "Neck pain", "Shoulder pain", "Knee pain", "Foot pain", "Hand pain"
        );
    }
}
