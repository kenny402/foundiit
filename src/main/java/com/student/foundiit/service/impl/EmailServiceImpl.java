package com.student.foundiit.service.impl;

import com.student.foundiit.model.Claim;
import com.student.foundiit.model.Item;
import com.student.foundiit.model.Match;
import com.student.foundiit.model.User;
import com.student.foundiit.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8081}")
    private String appBaseUrl;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Async
    @Override
    public void sendWelcomeEmail(User user) {
        if (mailSender == null) {
            logger.info("Email skipped: {}", user.getEmail());
            return;
        }
        sendHtmlMail(
                user.getEmail(),
                "Welcome to FoundIt+",
                buildHtmlEmail(
                        "Welcome " + user.getName(),
                        "Thank you for joining FoundIt+!",
                        "/dashboard",
                        "Go to Dashboard"
                )
        );
    }

    @Async
    @Override
    public void sendItemReportedEmail(User user, Item item) {
        if (mailSender == null) return;
        sendHtmlMail(
                user.getEmail(),
                "Item Reported",
                buildHtmlEmail(
                        "Item Reported Successfully",
                        "Your item '" + item.getTitle() + "' has been reported.",
                        "/items/" + item.getId(),
                        "View Item"
                )
        );
    }

    @Async
    @Override
    public void sendMatchFoundEmail(Match match) {
        if (mailSender == null) return;
        sendHtmlMail(
                match.getLostItem().getReportedBy().getEmail(),
                "Match Found - FoundIt+",
                buildHtmlEmail(
                        "Potential Match Found!",
                        "A match was found for: " +
                                match.getLostItem().getTitle(),
                        "/items/" + match.getLostItem().getId(),
                        "View Details"
                )
        );
        sendHtmlMail(
                match.getFoundItem().getReportedBy().getEmail(),
                "Match Found - FoundIt+",
                buildHtmlEmail(
                        "Potential Match Found!",
                        "A match was found for: " +
                                match.getFoundItem().getTitle(),
                        "/items/" + match.getFoundItem().getId(),
                        "View Details"
                )
        );
    }

    @Async
    @Override
    public void sendClaimSubmittedEmail(Item item, Claim claim) {
        if (mailSender == null) return;
        sendHtmlMail(
                claim.getClaimant().getEmail(),
                "Claim Submitted",
                buildHtmlEmail(
                        "Claim Submitted",
                        "Your claim for '" + item.getTitle() +
                                "' is pending review.",
                        "/claims/my-claims",
                        "My Claims"
                )
        );
    }

    @Async
    @Override
    public void sendClaimReviewedEmail(Claim claim) {
        if (mailSender == null) return;
        sendHtmlMail(
                claim.getClaimant().getEmail(),
                "Claim Processed",
                buildHtmlEmail(
                        "Claim Reviewed",
                        "Your claim for '" +
                                claim.getItem().getTitle() +
                                "' has been " +
                                claim.getStatus().getDisplayName(),
                        "/claims/my-claims",
                        "View Claim"
                )
        );
    }

    private void sendHtmlMail(
            String to, String subject, String html) {
        try {
            MimeMessage message =
                    mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Email failed to {}: {}",
                    to, e.getMessage());
        }
    }

    private String buildHtmlEmail(
            String title, String body,
            String ctaUrl, String ctaLabel) {
        return "<div style='font-family:Arial,sans-serif;"
                + "max-width:600px;margin:auto;padding:20px;"
                + "border:1px solid #ddd;'>"
                + "<h2 style='color:#0d6efd;'>" + title + "</h2>"
                + "<p>" + body + "</p>"
                + "<a href='" + appBaseUrl + ctaUrl + "'"
                + " style='background:#0d6efd;color:white;"
                + "padding:10px 20px;text-decoration:none;"
                + "border-radius:5px;'>" + ctaLabel + "</a>"
                + "<hr style='margin-top:30px;'>"
                + "<p style='color:#777;font-size:12px;'>"
                + "FoundIt+ &copy; 2025</p></div>";
    }
}
