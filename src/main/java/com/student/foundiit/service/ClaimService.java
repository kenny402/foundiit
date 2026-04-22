package com.student.foundiit.service;

import com.student.foundiit.model.Claim;
import com.student.foundiit.model.ClaimStatus;
import com.student.foundiit.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClaimService {
    Claim submitClaim(Long itemId, String proofDescription, MultipartFile proofImage, User claimant);
    Claim findById(Long claimId);
    Claim approveClaim(Long claimId, User reviewer, String notes);
    Claim rejectClaim(Long claimId, User reviewer, String notes);
    List<Claim> getClaimsByUser(User user);
    Page<Claim> getPendingClaims(Pageable pageable);
    Page<Claim> getAllClaims(Pageable pageable);
    long countByStatus(ClaimStatus status);
}
