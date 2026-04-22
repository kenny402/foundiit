package com.student.foundiit.repository;

import com.student.foundiit.model.Claim;
import com.student.foundiit.model.ClaimStatus;
import com.student.foundiit.model.Item;
import com.student.foundiit.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByClaimantOrderByCreatedAtDesc(User claimant);
    
    Page<Claim> findByStatusOrderByCreatedAtDesc(ClaimStatus status, Pageable pageable);
    
    long countByStatus(ClaimStatus status);
    
    boolean existsByItemAndClaimantAndStatusIn(
            Item item, User claimant, List<ClaimStatus> statuses);
}
