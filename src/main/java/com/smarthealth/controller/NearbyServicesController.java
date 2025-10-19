package com.smarthealth.controller;

import com.smarthealth.model.User;
import com.smarthealth.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Nearby Services Controller
 * Handles nearby hospitals and pharmacies functionality
 */
@Controller
@RequestMapping("/nearby-services")
public class NearbyServicesController {

    @Autowired
    private HealthDataService healthDataService;

    /**
     * Nearby services main page
     */
    @GetMapping
    public String nearbyServices(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "nearby-services";
    }

    /**
     * Find nearby hospitals
     */
    @PostMapping("/hospitals")
    public String findNearbyHospitals(@RequestParam double latitude,
                                    @RequestParam double longitude,
                                    @RequestParam(defaultValue = "5000") int radius,
                                    Model model,
                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> result = healthDataService.findNearbyHospitals(latitude, longitude, radius);
        
        model.addAttribute("user", user);
        model.addAttribute("result", result);
        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);
        model.addAttribute("radius", radius);
        model.addAttribute("serviceType", "hospitals");
        
        return "nearby-results";
    }

    /**
     * Find nearby pharmacies
     */
    @PostMapping("/pharmacies")
    public String findNearbyPharmacies(@RequestParam double latitude,
                                     @RequestParam double longitude,
                                     @RequestParam(defaultValue = "5000") int radius,
                                     Model model,
                                     Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> result = healthDataService.findNearbyPharmacies(latitude, longitude, radius);
        
        model.addAttribute("user", user);
        model.addAttribute("result", result);
        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);
        model.addAttribute("radius", radius);
        model.addAttribute("serviceType", "pharmacies");
        
        return "nearby-results";
    }

    /**
     * Location input page
     */
    @GetMapping("/location")
    public String locationInput(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "location-input";
    }

    /**
     * Hospitals page
     */
    @GetMapping("/hospitals")
    public String hospitals(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "hospitals";
    }

    /**
     * Pharmacies page
     */
    @GetMapping("/pharmacies")
    public String pharmacies(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "pharmacies";
    }
}
