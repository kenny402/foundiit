package com.student.foundiit.controller;

import com.student.foundiit.dto.ClaimDto;
import com.student.foundiit.model.User;
import com.student.foundiit.repository.UserRepository;
import com.student.foundiit.service.ClaimService;
import com.student.foundiit.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;
    private final ItemService itemService;
    private final UserRepository userRepository;

    public ClaimController(ClaimService claimService, ItemService itemService,
                           UserRepository userRepository) {
        this.claimService = claimService;
        this.itemService = itemService;
        this.userRepository = userRepository;
    }

    @GetMapping("/submit/{itemId}")
    public String submitForm(@PathVariable Long itemId, Model model) {
        ClaimDto claimDto = new ClaimDto();
        claimDto.setItemId(itemId);
        model.addAttribute("claimDto", claimDto);
        model.addAttribute("item", itemService.findById(itemId));
        return "claims/submit";
    }

    @PostMapping("/submit/{itemId}")
    public String submit(@PathVariable Long itemId,
                         @Valid @ModelAttribute ClaimDto claimDto,
                         BindingResult result,
                         @RequestParam("proofImage") MultipartFile proofImage,
                         Model model) {
        User user = getCurrentUser();
        if (user == null) return "redirect:/login";
        if (result.hasErrors()) {
            model.addAttribute("item", itemService.findById(itemId));
            return "claims/submit";
        }
        try {
            claimService.submitClaim(itemId, claimDto.getProofDescription(), proofImage, user);
            return "redirect:/claims/my-claims";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("item", itemService.findById(itemId));
            return "claims/submit";
        }
    }

    @GetMapping("/my-claims")
    public String myClaims(Model model) {
        User user = getCurrentUser();
        if (user == null) return "redirect:/login";
        model.addAttribute("claims", claimService.getClaimsByUser(user));
        return "claims/my-claims";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth.getName().equals("anonymousUser")) return null;
        return userRepository.findByEmailIgnoreCase(auth.getName().trim()).orElse(null);
    }
}
