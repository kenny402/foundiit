package com.student.foundiit.repository;

import com.student.foundiit.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByType(Item.ItemType type);

    List<Item> findByUserId(Long userId);
}
