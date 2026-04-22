package com.student.foundiit.repository;

import com.student.foundiit.model.Category;
import com.student.foundiit.model.Item;
import com.student.foundiit.model.ItemStatus;
import com.student.foundiit.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByStatusOrderByCreatedAtDesc(ItemStatus status, Pageable pageable);
    
    Page<Item> findByStatusAndCategoryOrderByCreatedAtDesc(
            ItemStatus status, Category category, Pageable pageable);
    
    List<Item> findByStatusAndCategory(ItemStatus status, Category category);
    
    Page<Item> findByReportedByOrderByCreatedAtDesc(User user, Pageable pageable);
    
    long countByStatus(ItemStatus status);

    @Query("SELECT i FROM Item i WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Item> searchItems(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE Item i SET i.views = i.views + 1 WHERE i.id = :id")
    void incrementViews(@Param("id") Long id);
}
