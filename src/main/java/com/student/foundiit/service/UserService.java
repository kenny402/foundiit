package com.student.foundiit.service;

import com.student.foundiit.model.Role;
import com.student.foundiit.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User registerUser(String name, String email, String phone, String password);
    User createAdmin(String name, String email, String phone, String password);
    User findByEmail(String email);
    User findById(Long id);
    User updateRole(Long id, Role role);
    User toggleEnabled(Long id);
    Page<User> getAllUsers(Pageable pageable);
    Page<User> searchUsers(String keyword, Pageable pageable);
    long countByRole(Role role);
}
