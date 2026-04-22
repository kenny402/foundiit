package com.student.foundiit.service.impl;

import com.student.foundiit.model.*;
import com.student.foundiit.repository.ClaimRepository;
import com.student.foundiit.repository.ItemRepository;
import com.student.foundiit.service.ClaimService;
import com.student.foundiit.service.FileUploadService;
import com.student.foundiit.service.NotificationService;
import com.student.foundiit.service.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final FileUploadService fileUploadService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public ClaimServiceImpl(ClaimRepository claimRepository, ItemRepository itemRepository, FileUploadService fileUploadService, NotificationService notificationService, EmailService emailService) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.fileUploadService = fileUploadService;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Override
    public Claim submitClaim(Long itemId, String proofDescription, MultipartFile proofImage, User claimant) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (item.getStatus() != ItemStatus.FOUND && item.getStatus() != ItemStatus.LOST) {
            throw new RuntimeException("Item is not available for claiming");
        }
        
        if (item.getReportedBy().getId().equals(claimant.getId())) {
            throw new RuntimeException("You cannot claim your own item");
        }
        
        if (claimRepository.existsByItemAndClaimantAndStatusIn(item, claimant, List.of(ClaimStatus.PENDING, ClaimStatus.APPROVED))) {
            throw new RuntimeException("You already have a pending or approved claim for this item");
        }

        String imagePath = null;
        try {
            imagePath = fileUploadService.saveFile(proofImage, "claims");
        } catch (IOException e) {
            throw new RuntimeException("Could not save proof image", e);
        }

        Claim claim = Claim.builder()
                .item(item)
                .claimant(claimant)
                .proofDescription(proofDescription)
                .proofImagePath(imagePath)
                .status(ClaimStatus.PENDING)
                .claimDate(LocalDate.now())
                .build();

        Claim savedClaim = claimRepository.save(claim);
        notificationService.notifyClaimSubmitted(savedClaim);
        emailService.sendClaimSubmittedEmail(item, savedClaim);
        
        return savedClaim;
    }

    @Override
    public Claim findById(Long claimId) {
        return claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
    }

    @Override
    public Claim approveClaim(Long claimId, User reviewer, String notes) {
        Claim claim = findById(claimId);
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setReviewedBy(reviewer);
        claim.setReviewedAt(LocalDateTime.now());
        claim.setReviewNotes(notes);
        
        Item item = claim.getItem();
        item.setStatus(ItemStatus.CLAIMED);
        itemRepository.save(item);
        
        Claim savedClaim = claimRepository.save(claim);
        notificationService.notifyClaimReviewed(savedClaim);
        emailService.sendClaimReviewedEmail(savedClaim);
        
        return savedClaim;
    }

    @Override
    public Claim rejectClaim(Long claimId, User reviewer, String notes) {
        Claim claim = findById(claimId);
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setReviewedBy(reviewer);
        claim.setReviewedAt(LocalDateTime.now());
        claim.setReviewNotes(notes);
        
        Claim savedClaim = claimRepository.save(claim);
        notificationService.notifyClaimReviewed(savedClaim);
        emailService.sendClaimReviewedEmail(savedClaim);
        
        return savedClaim;
    }

    @Override
    public List<Claim> getClaimsByUser(User user) {
        return claimRepository.findByClaimantOrderByCreatedAtDesc(user);
    }

    @Override
    public Page<Claim> getPendingClaims(Pageable pageable) {
        return claimRepository.findByStatusOrderByCreatedAtDesc(ClaimStatus.PENDING, pageable);
    }

    @Override
    public Page<Claim> getAllClaims(Pageable pageable) {
        return claimRepository.findAll(pageable);
    }

    @Override
    public long countByStatus(ClaimStatus status) {
        return claimRepository.countByStatus(status);
    }
}
