package com.student.foundiit.service.impl;

import com.student.foundiit.model.*;
import com.student.foundiit.repository.NotificationRepository;
import com.student.foundiit.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void createNotification(User user, String title, String message, String link) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .link(link)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public void notifyMatch(Match match) {
        String msg = "A potential match was found for your item: " + match.getLostItem().getTitle();
        createNotification(match.getLostItem().getReportedBy(), "Match Found!", msg,
                "/items/" + match.getLostItem().getId());

        String msgFound = "A potential match was found for your item: " + match.getFoundItem().getTitle();
        createNotification(match.getFoundItem().getReportedBy(), "Match Found!", msgFound,
                "/items/" + match.getFoundItem().getId());
    }

    @Override
    public void notifyClaimSubmitted(Claim claim) {
        // Notify the claimant that their claim was submitted
        String msg = "Your claim for '" + claim.getItem().getTitle() + "' has been submitted and is awaiting review.";
        createNotification(claim.getClaimant(), "Claim Submitted", msg, "/claims/my-claims");

        // Also notify the item reporter that someone claimed their item
        if (claim.getItem().getReportedBy() != null
                && !claim.getItem().getReportedBy().getId().equals(claim.getClaimant().getId())) {
            String reporterMsg = claim.getClaimant().getName() + " has submitted a claim for your item '"
                    + claim.getItem().getTitle() + "'. A moderator will review it shortly.";
            createNotification(
                    claim.getItem().getReportedBy(),
                    "New Claim on Your Item",
                    reporterMsg,
                    "/items/" + claim.getItem().getId());
        }
    }

    @Override
    public void notifyClaimReviewed(Claim claim) {
        String itemTitle = claim.getItem().getTitle();
        String statusText = claim.getStatus().getDisplayName().toLowerCase();

        // 1. Always notify the claimant about the review result
        String claimantMsg = "Your claim for '" + itemTitle + "' has been " + statusText + ".";
        if (claim.getStatus() == ClaimStatus.APPROVED) {
            claimantMsg += " You can now arrange to collect your item.";
        }
        createNotification(claim.getClaimant(), "Claim " + claim.getStatus().getDisplayName(), claimantMsg,
                "/claims/my-claims");

        // 2. If approved, also notify the item reporter (owner) that someone claimed
        // their item
        if (claim.getStatus() == ClaimStatus.APPROVED && claim.getItem().getReportedBy() != null) {
            String reporterMsg = "Great news! The claim for your item '" + itemTitle
                    + "' by " + claim.getClaimant().getName()
                    + " has been approved. Your item has been successfully matched!";
            createNotification(
                    claim.getItem().getReportedBy(),
                    "Item Claimed Successfully!",
                    reporterMsg,
                    "/items/" + claim.getItem().getId());
        }

        // 3. If rejected, optionally notify the reporter that a claim was rejected
        if (claim.getStatus() == ClaimStatus.REJECTED && claim.getItem().getReportedBy() != null) {
            String reporterMsg = "A claim for your item '" + itemTitle
                    + "' was reviewed and rejected. Your item is still available.";
            createNotification(
                    claim.getItem().getReportedBy(),
                    "Claim Rejected",
                    reporterMsg,
                    "/items/" + claim.getItem().getId());
        }
    }

    @Override
    public void notifyItemReported(Item item) {
        String msg = "Your item '" + item.getTitle() + "' has been successfully reported.";
        createNotification(item.getReportedBy(), "Item Reported", msg, "/items/" + item.getId());
    }

    @Override
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsRead(user.getId());
    }

    @Override
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
