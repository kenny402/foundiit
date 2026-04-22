package com.student.foundiit.service;

import com.student.foundiit.model.Claim;
import com.student.foundiit.model.Item;
import com.student.foundiit.model.Match;
import com.student.foundiit.model.Notification;
import com.student.foundiit.model.User;

import java.util.List;

public interface NotificationService {
    void createNotification(User user, String title, String message, String link);
    void notifyMatch(Match match);
    void notifyClaimSubmitted(Claim claim);
    void notifyClaimReviewed(Claim claim);
    void notifyItemReported(Item item);
    List<Notification> getUserNotifications(User user);
    long getUnreadCount(User user);
    void markAllAsRead(User user);
    void markAsRead(Long id);
}
