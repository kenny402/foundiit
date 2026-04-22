package com.student.foundiit.service;

import com.student.foundiit.model.Claim;
import com.student.foundiit.model.Item;
import com.student.foundiit.model.Match;
import com.student.foundiit.model.User;

public interface EmailService {
    void sendWelcomeEmail(User user);
    void sendItemReportedEmail(User user, Item item);
    void sendMatchFoundEmail(Match match);
    void sendClaimSubmittedEmail(Item item, Claim claim);
    void sendClaimReviewedEmail(Claim claim);
}
