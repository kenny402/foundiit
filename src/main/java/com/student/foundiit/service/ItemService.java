package com.student.foundiit.service;

import com.student.foundiit.model.Item;
import com.student.foundiit.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item saveItem(Item item, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(file.getInputStream(), uploadPath.resolve(fileName));
            item.setImage(fileName);
        }
        return itemRepository.save(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getItemsByType(Item.ItemType type) {
        return itemRepository.findByType(type);
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    public void updateStatus(Long id, Item.Status status) {
        itemRepository.findById(id).ifPresent(item -> {
            item.setStatus(status);
            itemRepository.save(item);
        });
    }
}
