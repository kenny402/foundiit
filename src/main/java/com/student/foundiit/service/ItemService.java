package com.student.foundiit.service;

import com.student.foundiit.model.Item;
import com.student.foundiit.model.ItemStatus;
import com.student.foundiit.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface ItemService {
    Item reportItem(String title, String description, Long categoryId, String color, String brand,
                   ItemStatus status, String location, LocalDate dateCreated, MultipartFile image, User reporter);
    Item findById(Long id);
    void closeItem(Long id, User user);
    void deleteItem(Long id);
    Page<Item> getAllActiveItems(Pageable pageable);
    Page<Item> getItemsByStatus(ItemStatus status, Pageable pageable);
    Page<Item> getItemsByUser(User user, Pageable pageable);
    Page<Item> searchItems(String keyword, Pageable pageable);
    void incrementViews(Long id);
    long countByStatus(ItemStatus status);
}
