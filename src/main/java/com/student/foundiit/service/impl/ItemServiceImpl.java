package com.student.foundiit.service.impl;

import com.student.foundiit.model.Category;
import com.student.foundiit.model.Item;
import com.student.foundiit.model.ItemStatus;
import com.student.foundiit.model.User;
import com.student.foundiit.repository.CategoryRepository;
import com.student.foundiit.repository.ItemRepository;
import com.student.foundiit.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final FileUploadService fileUploadService;
    private final MatchService matchService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public ItemServiceImpl(ItemRepository itemRepository, CategoryRepository categoryRepository,
            FileUploadService fileUploadService, MatchService matchService, NotificationService notificationService,
            EmailService emailService) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.fileUploadService = fileUploadService;
        this.matchService = matchService;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Override
    public Item reportItem(String title, String description, Long categoryId, String color, String brand,
            ItemStatus status, String location, LocalDate dateCreated, MultipartFile image, User reporter) {
        logger.info("Starting item report: title='{}', status='{}', category_id={}, reporter_id={}",
                title, status, categoryId, reporter != null ? reporter.getId() : "null");

        // Validate reporter
        if (reporter == null) {
            logger.error("Cannot report item: no authenticated user");
            throw new RuntimeException("User must be authenticated to report items");
        }

        // Fetch category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.error("Category not found: categoryId={}", categoryId);
                    return new RuntimeException("Category not found");
                });

        // Handle file upload
        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            try {
                logger.info("Uploading image for item: filename='{}'", image.getOriginalFilename());
                imagePath = fileUploadService.saveFile(image, "items");
                logger.info("Image uploaded successfully: imagePath='{}'", imagePath);
            } catch (IOException e) {
                logger.warn("Failed to upload image for item: {}", e.getMessage());
                // Don't fail the entire report if image upload fails
            }
        }

        // Create Item entity using builder
        Item item = Item.builder()
                .title(title)
                .description(description)
                .category(category)
                .color(color)
                .brand(brand)
                .status(status)
                .location(location)
                .dateCreated(dateCreated != null ? dateCreated : LocalDate.now())
                .imagePath(imagePath)
                .reportedBy(reporter)
                .views(0)
                .build();

        logger.debug("Created item instance: {}", item);

        // Save item to database
        Item savedItem = itemRepository.save(item);
        logger.info("Item saved successfully to database: item_id={}, title='{}'", savedItem.getId(),
                savedItem.getTitle());

        // Trigger matching asynchronously (wrap in try-catch to not fail if matching
        // fails)
        try {
            matchService.findMatches(savedItem);
            logger.info("Matching process initiated for item: item_id={}", savedItem.getId());
        } catch (Exception e) {
            logger.warn("Failed to find matches for item: {}", e.getMessage());
        }

        // Send notifications (wrap in try-catch to not fail if notifications fail)
        try {
            notificationService.notifyItemReported(savedItem);
            logger.info("Notification sent for item: item_id={}", savedItem.getId());
        } catch (Exception e) {
            logger.warn("Failed to send notification for item: {}", e.getMessage());
        }

        // Send email (wrap in try-catch to not fail if email fails)
        try {
            emailService.sendItemReportedEmail(reporter, savedItem);
            logger.info("Email sent for item: item_id={}", savedItem.getId());
        } catch (Exception e) {
            logger.warn("Failed to send email for item: {}", e.getMessage());
        }

        logger.info("Item report completed successfully: item_id={}", savedItem.getId());
        return savedItem;
    }

    @Override
    public Item findById(Long id) {
        logger.debug("Finding item by id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Item not found: id={}", id);
                    return new RuntimeException("Item not found");
                });
    }

    @Override
    public void closeItem(Long id, User user) {
        logger.info("Closing item: item_id={}, user_id={}", id, user != null ? user.getId() : "null");
        Item item = findById(id);
        if (item.getReportedBy() != null && !item.getReportedBy().getId().equals(user.getId())
                && !user.getRole().name().equals("ADMIN")) {
            logger.error("User not authorized to close item: user_id={}, item_id={}", user.getId(), id);
            throw new RuntimeException("Not authorized to close this item");
        }
        item.setStatus(ItemStatus.CLOSED);
        itemRepository.save(item);
        logger.info("Item closed successfully: item_id={}", id);
    }

    @Override
    public void deleteItem(Long id) {
        logger.info("Deleting item: id={}", id);
        itemRepository.deleteById(id);
        logger.info("Item deleted successfully: id={}", id);
    }

    @Override
    public Page<Item> getAllActiveItems(Pageable pageable) {
        logger.debug("Fetching all active items: {}", pageable);
        return itemRepository.findAll(pageable);
    }

    @Override
    public Page<Item> getItemsByStatus(ItemStatus status, Pageable pageable) {
        logger.debug("Fetching items by status: status={}, {}", status, pageable);
        return itemRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Override
    public Page<Item> getItemsByUser(User user, Pageable pageable) {
        logger.debug("Fetching items by user: user_id={}, {}", user != null ? user.getId() : "null", pageable);
        return itemRepository.findByReportedByOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    public Page<Item> searchItems(String keyword, Pageable pageable) {
        logger.debug("Searching items: keyword='{}', {}", keyword, pageable);
        return itemRepository.searchItems(keyword, pageable);
    }

    @Override
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void incrementViews(Long id) {
        logger.debug("Incrementing views for item: id={}", id);
        itemRepository.incrementViews(id);
    }

    @Override
    public long countByStatus(ItemStatus status) {
        logger.debug("Counting items by status: status={}", status);
        return itemRepository.countByStatus(status);
    }
}
