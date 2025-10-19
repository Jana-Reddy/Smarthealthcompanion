package com.smarthealth.controller;

import com.smarthealth.model.User;
import com.smarthealth.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * First Aid Controller
 * Handles first aid guidance functionality
 */
@Controller
@RequestMapping("/first-aid")
public class FirstAidController {

    @Autowired
    private HealthDataService healthDataService;

    /**
     * First aid main page
     */
    @GetMapping
    public String firstAid(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<String> firstAidTypes = healthDataService.getAllFirstAidTypes();
        
        model.addAttribute("user", user);
        model.addAttribute("firstAidTypes", firstAidTypes);
        return "first-aid";
    }

    /**
     * Get first aid guidance for specific emergency type
     */
    @GetMapping("/guidance/{type}")
    public String getFirstAidGuidance(@PathVariable String type,
                                    Model model,
                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> guidance = healthDataService.getFirstAidGuidance(type);
        
        model.addAttribute("user", user);
        model.addAttribute("guidance", guidance);
        model.addAttribute("emergencyType", type);
        
        return "first-aid-guidance";
    }

    /**
     * Search first aid guidance
     */
    @PostMapping("/search")
    public String searchFirstAid(@RequestParam String emergencyType,
                                Model model,
                                Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> guidance = healthDataService.getFirstAidGuidance(emergencyType);
        
        model.addAttribute("user", user);
        model.addAttribute("guidance", guidance);
        model.addAttribute("emergencyType", emergencyType);
        
        return "first-aid-guidance";
    }

    /**
     * Emergency procedures page
     */
    @GetMapping("/emergency-procedures")
    public String emergencyProcedures(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<String> firstAidTypes = healthDataService.getAllFirstAidTypes();
        
        model.addAttribute("user", user);
        model.addAttribute("firstAidTypes", firstAidTypes);
        return "emergency-procedures";
    }

    /**
     * CPR guide page
     */
    @GetMapping("/cpr")
    public String cprGuide(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> cprGuidance = healthDataService.getFirstAidGuidance("CPR");
        
        model.addAttribute("user", user);
        model.addAttribute("guidance", cprGuidance);
        return "cpr-guide";
    }

    /**
     * Burns guide page
     */
    @GetMapping("/burns")
    public String burnsGuide(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> burnsGuidance = healthDataService.getFirstAidGuidance("Burns");
        
        model.addAttribute("user", user);
        model.addAttribute("guidance", burnsGuidance);
        return "burns-guide";
    }

    /**
     * Bleeding guide page
     */
    @GetMapping("/bleeding")
    public String bleedingGuide(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> bleedingGuidance = healthDataService.getFirstAidGuidance("Bleeding");
        
        model.addAttribute("user", user);
        model.addAttribute("guidance", bleedingGuidance);
        return "bleeding-guide";
    }

    /**
     * Choking guide page
     */
    @GetMapping("/choking")
    public String chokingGuide(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> chokingGuidance = healthDataService.getFirstAidGuidance("Choking");
        
        model.addAttribute("user", user);
        model.addAttribute("guidance", chokingGuidance);
        return "choking-guide";
    }
}
