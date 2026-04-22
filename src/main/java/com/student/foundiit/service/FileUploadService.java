package com.student.foundiit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    private final String UPLOAD_DIR = "./uploads/";

    public String saveFile(MultipartFile file, String subfolder) throws IOException {
        logger.info("Saving file: filename='{}', subfolder='{}'", file.getOriginalFilename(), subfolder);

        if (file.isEmpty()) {
            logger.warn("File is empty: filename='{}'", file.getOriginalFilename());
            return null;
        }

        String contentType = file.getContentType();
        logger.debug("File content type: {}", contentType);

        if (contentType == null || (!contentType.startsWith("image/jpeg") &&
                !contentType.startsWith("image/png") &&
                !contentType.startsWith("image/gif") &&
                !contentType.startsWith("image/webp"))) {
            logger.error("Invalid file type: {}", contentType);
            throw new IOException("Invalid file type. Only JPEG, PNG, GIF, and WEBP are allowed.");
        }

        String fileName = UUID.randomUUID().toString() + "_" + sanitizeFilename(file.getOriginalFilename());
        Path uploadPath = Paths.get(UPLOAD_DIR + subfolder);

        logger.debug("Upload path: {}", uploadPath);

        if (!Files.exists(uploadPath)) {
            logger.info("Creating upload directory: {}", uploadPath);
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        logger.debug("File will be saved at: {}", filePath);

        try {
            Files.copy(file.getInputStream(), filePath);
            logger.info("File saved successfully: {} (size: {} bytes)", filePath, file.getSize());
        } catch (IOException e) {
            logger.error("Failed to save file: {}", e.getMessage(), e);
            throw new IOException("Failed to save file: " + e.getMessage(), e);
        }

        String relativePath = subfolder + "/" + fileName;
        logger.info("Relative path for database: {}", relativePath);
        return relativePath;
    }

    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            logger.warn("Relative path is null or empty");
            return;
        }
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(relativePath);
            logger.info("Attempting to delete file: {}", filePath);
            Files.deleteIfExists(filePath);
            logger.info("File deleted successfully: {}", filePath);
        } catch (IOException e) {
            logger.warn("Failed to delete file: {}, error: {}", relativePath, e.getMessage());
        }
    }

    /**
     * Sanitize filename to prevent path traversal and other issues
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "file";
        }
        // Remove any path separators and suspicious characters
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
