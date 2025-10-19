package com.smarthealth.controller;

import com.smarthealth.model.ContactMessage;
import com.smarthealth.model.User;
import com.smarthealth.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Contact Controller
 * Handles contact form submissions and email functionality
 */
@Controller
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private MailService mailService;

    /**
     * Process contact form submission
     */
    @PostMapping("/submit")
    public String submitContactForm(@Valid ContactMessage contactMessage,
                                  RedirectAttributes redirectAttributes) {
        try {
            mailService.sendContactFormEmail(contactMessage);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Thank you for your message! We will get back to you soon.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to send message. Please try again later.");
        }
        
        return "redirect:/contact";
    }

    /**
     * Contact form page
     */
    @GetMapping
    public String contactForm(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "contact";
    }
}
