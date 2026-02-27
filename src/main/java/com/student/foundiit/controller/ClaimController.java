package com.student.foundiit.controller;

import com.student.foundiit.model.Claim;
import com.student.foundiit.model.Item;
import com.student.foundiit.model.User;
import com.student.foundiit.service.ClaimService;
import com.student.foundiit.service.ItemService;
import com.student.foundiit.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;
    private final ItemService itemService;
    private final UserService userService;

    public ClaimController(ClaimService claimService, ItemService itemService, UserService userService) {
        this.claimService = claimService;
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping("/item/{itemId}")
    public String showClaimForm(@PathVariable Long itemId, Model model) {
        Item item = itemService.findById(itemId);
        model.addAttribute("item", item);
        model.addAttribute("claim", new Claim());
        return "claim-form";
    }

    @PostMapping("/submit")
    public String submitClaim(@ModelAttribute("claim") Claim claim,
            @RequestParam("itemId") Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        Item item = itemService.findById(itemId);

        claim.setUser(user);
        claim.setItem(item);
        claimService.submitClaim(claim);

        return "redirect:/dashboard?claimSubmitted";
    }
}
