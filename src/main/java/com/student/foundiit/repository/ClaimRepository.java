package com.student.foundiit.repository;

import com.student.foundiit.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByUserId(Long userId);

    List<Claim> findByItemId(Long itemId);
}
