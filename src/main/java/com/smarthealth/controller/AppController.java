package com.smarthealth.controller;

import com.smarthealth.model.User;
import com.smarthealth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

/**
 * Main Application Controller
 * Handles home page, login, registration, about, privacy, and terms pages.
 */
@Controller
public class AppController {

    @Autowired
    private UserService userService;

    /**
     * Home page
     */
    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
        }
        return "index";
    }

    /**
     * Login page
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "login";
    }

    /**
     * Registration page
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Process registration
     */
    @PostMapping("/register")
    public String processRegistration(@Valid User user, BindingResult result,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(user.getName(), user.getEmail(), user.getPassword());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please login with your credentials.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * About page
     */
    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    /**
     * Privacy policy page
     */
    @GetMapping("/privacy")
    public String privacy(Model model) {
        return "privacy";
    }

    /**
     * Terms of service page
     */
    @GetMapping("/terms")
    public String terms(Model model) {
        return "terms";
    }
}
