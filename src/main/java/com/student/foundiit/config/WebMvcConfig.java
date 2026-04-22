package com.student.foundiit.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get("./uploads/items"));
            Files.createDirectories(Paths.get("./uploads/claims"));
            Files.createDirectories(Paths.get("./uploads/profiles"));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }
}
