package com.student.foundiit.service;

import com.student.foundiit.dto.ItemDto;
import com.student.foundiit.model.Category;
import com.student.foundiit.model.Item;
import com.student.foundiit.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Component
public class ItemMapper {

    /**
     * Maps ItemDto and related data to Item entity
     */
    public Item mapToItem(ItemDto itemDto, Category category, User reportedBy, String imagePath) {
        Item item = new Item();
        item.setTitle(itemDto.getTitle());
        item.setDescription(itemDto.getDescription());
        item.setCategory(category);
        item.setColor(itemDto.getColor());
        item.setBrand(itemDto.getBrand());
        item.setStatus(itemDto.getStatus());
        item.setLocation(itemDto.getLocation());
        item.setImagePath(imagePath);
        item.setDateCreated(itemDto.getDateCreated() != null ? itemDto.getDateCreated() : LocalDate.now());
        item.setReportedBy(reportedBy);
        item.setViews(0);
        return item;
    }
}

