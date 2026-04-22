package com.student.foundiit.controller;

import com.student.foundiit.model.Claim;
import com.student.foundiit.model.ClaimStatus;
import com.student.foundiit.model.User;
import com.student.foundiit.repository.UserRepository;
import com.student.foundiit.service.ClaimService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/moderator")
@PreAuthorize("hasAnyRole('MODERATOR','ADMIN','moderator','admin')")
public class ModeratorController {

    private final ClaimService claimService;
    private final UserRepository userRepository;

    public ModeratorController(ClaimService claimService, UserRepository userRepository) {
        this.claimService = claimService;
        this.userRepository = userRepository;
    }

    @GetMapping("/claims")
    public String pendingClaims(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Claim> claimPage = claimService.getPendingClaims(PageRequest.of(page, 10));
        model.addAttribute("claims", claimPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", claimPage.getTotalPages());
        model.addAttribute("pendingCount", claimService.countByStatus(ClaimStatus.PENDING));
        model.addAttribute("approvedCount", claimService.countByStatus(ClaimStatus.APPROVED));
        model.addAttribute("rejectedCount", claimService.countByStatus(ClaimStatus.REJECTED));
        return "moderator/dashboard";
    }

    @GetMapping("/claims/{id}/review")
    public String reviewClaim(@PathVariable Long id, Model model) {
        Claim claim = claimService.findById(id);
        model.addAttribute("claim", claim);
        return "moderator/review-claim";
    }

    @PostMapping("/claims/{id}/approve")
    public String approve(@PathVariable Long id, @RequestParam(required = false) String notes) {
        User user = getCurrentUser();
        claimService.approveClaim(id, user, notes != null ? notes : "Approved");
        return "redirect:/moderator/claims";
    }

    @PostMapping("/claims/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam(required = false) String notes) {
        User user = getCurrentUser();
        claimService.rejectClaim(id, user, notes != null ? notes : "Rejected");
        return "redirect:/moderator/claims";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth.getName().equals("anonymousUser")) return null;
        return userRepository.findByEmailIgnoreCase(auth.getName().trim()).orElse(null);
    }
}
