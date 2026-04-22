package com.student.foundiit.config;

import com.student.foundiit.model.Category;
import com.student.foundiit.repository.CategoryRepository;
import com.student.foundiit.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final CategoryRepository categoryRepository;

    public DataInitializer(UserService userService, CategoryRepository categoryRepository) {
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        // Create Admin
        try {
            userService.createAdmin("Admin", "test@test.com", null, "imena1234!");
            System.out.println("Admin ready: test@test.com / imena1234!");
        } catch (Exception e) {
            System.out.println("Admin setup: " + e.getMessage());
        }

        // Create Default Categories
        try {
            if (categoryRepository.count() == 0) {
                List<String> defaultCategories = List.of(
                        "Electronics", "Personal Items", "Documents", "Accessories", "Others"
                );
                for (String name : defaultCategories) {
                    Category category = new Category();
                    category.setCategoryName(name);
                    categoryRepository.save(category);
                }
                System.out.println("Default categories seeded!");
            }
        } catch (Exception e) {
            System.out.println("Category seeding error: " + e.getMessage());
        }
    }
}