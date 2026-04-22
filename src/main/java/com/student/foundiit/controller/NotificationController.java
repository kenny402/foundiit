package com.student.foundiit.controller;

import com.student.foundiit.model.User;
import com.student.foundiit.repository.UserRepository;
import com.student.foundiit.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService,
                                   UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model) {
        User user = getCurrentUser();
        if (user == null) return "redirect:/login";
        model.addAttribute("notifications", notificationService.getUserNotifications(user));
        notificationService.markAllAsRead(user);
        return "notifications/list";
    }

    @PostMapping("/mark-all-read")
    public String markAllRead() {
        User user = getCurrentUser();
        if (user == null) return "redirect:/login";
        notificationService.markAllAsRead(user);
        return "redirect:/notifications";
    }

    @PostMapping("/mark-read/{id}")
    @ResponseBody
    public Map<String, Object> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Map.of("success", true);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth.getName().equals("anonymousUser")) return null;
        return userRepository.findByEmailIgnoreCase(auth.getName().trim()).orElse(null);
    }
}
