package com.student.foundiit.controller;

import com.student.foundiit.dto.UserRegistrationDto;
import com.student.foundiit.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRegistrationDto dto,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        if (dto.getConfirmPassword() != null
                && !dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("registrationError", "Passwords do not match!");
            return "auth/register";
        }
        try {
            userService.registerUser(dto.getName(), dto.getEmail(),
                    dto.getPhone(), dto.getPassword());
            return "redirect:/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("registrationError", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return "redirect:/login";
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            String role = auth.getAuthority();
            if (role.equals("ROLE_ADMIN") || role.equals("ROLE_admin"))
                return "redirect:/admin/dashboard";
            if (role.equals("ROLE_MODERATOR") || role.equals("ROLE_moderator"))
                return "redirect:/moderator/claims";
        }
        return "redirect:/items/list";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}