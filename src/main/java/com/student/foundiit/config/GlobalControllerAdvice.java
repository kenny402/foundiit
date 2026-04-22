package com.student.foundiit.config;

import com.student.foundiit.repository.NotificationRepository;
import com.student.foundiit.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public GlobalControllerAdvice(NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addUnreadCount(Model model) {
        model.addAttribute("unreadCount", 0L);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !auth.getName().equals("anonymousUser")) {
                userRepository.findByEmailIgnoreCase(auth.getName().trim()).ifPresent(user -> {
                    model.addAttribute("unreadCount",
                            notificationRepository.countByUserAndIsReadFalse(user));
                    model.addAttribute("currentUser", user);
                });
            }
        } catch (Exception e) {
            // silently ignore
        }
    }
}