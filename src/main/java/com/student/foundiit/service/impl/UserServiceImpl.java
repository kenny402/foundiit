package com.student.foundiit.service.impl;

import com.student.foundiit.model.Role;
import com.student.foundiit.model.User;
import com.student.foundiit.repository.UserRepository;
import com.student.foundiit.service.EmailService;
import com.student.foundiit.service.UserService;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User registerUser(String name, String email, String phone, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .name(name)
                .email(normalizedEmail)
                .phone(normalizePhone(phone))
                .password(passwordEncoder.encode(password))
                .role(Role.user)
                .enabled(true)
                .build();
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(savedUser);
        return savedUser;
    }

    @Override
    public User createAdmin(String name, String email, String phone, String password) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
        if (user == null) {
            user = User.builder()
                    .name(name)
                    .email(normalizedEmail)
                    .phone(normalizePhone(phone))
                    .password(passwordEncoder.encode(password))
                    .role(Role.admin)
                    .enabled(true)
                    .build();
            System.out.println("Creating new admin user");
        } else {
            user.setEmail(normalizedEmail);
            user.setPhone(normalizePhone(phone));
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(Role.admin);
            user.setEnabled(true);
            System.out.println("Updating existing admin user");
        }
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email)).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User updateRole(Long id, Role role) {
        User user = findById(id);
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    public User toggleEnabled(Long id) {
        User user = findById(id);
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            keyword, keyword, pageable);
    }

    @Override
    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        String normalizedEmail = normalizeEmail(email);
        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with email: " + email));
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        String normalizedPhone = phone.trim();
        return normalizedPhone.isEmpty() ? null : normalizedPhone;
    }
}
