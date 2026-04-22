package com.student.foundiit.controller;

import com.student.foundiit.dto.ItemDto;
import com.student.foundiit.model.Item;
import com.student.foundiit.model.ItemStatus;
import com.student.foundiit.model.User;
import com.student.foundiit.repository.CategoryRepository;
import com.student.foundiit.repository.UserRepository;
import com.student.foundiit.service.ItemService;
import com.student.foundiit.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final CategoryRepository categoryRepository;
    private final MatchService matchService;
    private final UserRepository userRepository;

    public ItemController(ItemService itemService, CategoryRepository categoryRepository,
            MatchService matchService, UserRepository userRepository) {
        this.itemService = itemService;
        this.categoryRepository = categoryRepository;
        this.matchService = matchService;
        this.userRepository = userRepository;
    }

    @GetMapping({ "", "/", "/list" })
    public String listItems(@RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false, name = "type") ItemStatus type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {
        ItemStatus effectiveStatus = status != null ? status : type;
        Page<Item> itemPage;
        if (keyword != null && !keyword.isEmpty()) {
            itemPage = itemService.searchItems(keyword, PageRequest.of(page, 9));
        } else if (effectiveStatus != null) {
            itemPage = itemService.getItemsByStatus(effectiveStatus, PageRequest.of(page, 9));
        } else {
            itemPage = itemService.getAllActiveItems(PageRequest.of(page, 9));
        }
        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("status", effectiveStatus);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categoryRepository.findAllByOrderByCategoryNameAsc());
        return "items/list";
    }

    @GetMapping("/{id:\\d+}")
    public String detail(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id);

        // Non-critical operations wrapped in try-catch to prevent 500 errors
        try {
            itemService.incrementViews(id);
        } catch (Exception e) {
            // Log and continue
        }

        User user = getCurrentUser();
        model.addAttribute("item", item);

        try {
            model.addAttribute("matches", matchService.getMatchesForItem(item));
        } catch (Exception e) {
            // Log and continue
        }

        boolean canClaim = user != null && item.getReportedBy() != null
                && !item.getReportedBy().getId().equals(user.getId());
        model.addAttribute("canClaim", canClaim);
        return "items/detail";
    }

    @GetMapping("/report")
    public String reportForm(Model model) {
        model.addAttribute("itemDto", new ItemDto());
        model.addAttribute("categories", categoryRepository.findAllByOrderByCategoryNameAsc());
        return "items/report";
    }

    @PostMapping("/report")
    public String report(@Valid @ModelAttribute ItemDto itemDto,
            BindingResult result,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Model model) {
        User user = getCurrentUser();
        if (user == null)
            return "redirect:/login";
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAllByOrderByCategoryNameAsc());
            return "items/report";
        }
        try {
            itemService.reportItem(itemDto.getTitle(), itemDto.getDescription(),
                    itemDto.getCategoryId(), itemDto.getColor(), itemDto.getBrand(),
                    itemDto.getStatus(), itemDto.getLocation(),
                    itemDto.getDateCreated(), image, user);
            return "redirect:/items/my-items?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Error reporting item: " + e.getMessage());
            model.addAttribute("categories", categoryRepository.findAllByOrderByCategoryNameAsc());
            return "items/report";
        }
    }

    @GetMapping("/my-items")
    public String myItems(@RequestParam(defaultValue = "0") int page, Model model) {
        User user = getCurrentUser();
        if (user == null)
            return "redirect:/login";
        Page<Item> itemPage = itemService.getItemsByUser(user, PageRequest.of(page, 10));
        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        return "items/my-items";
    }

    @PostMapping("/close/{id}")
    public String closeItem(@PathVariable Long id) {
        User user = getCurrentUser();
        if (user == null)
            return "redirect:/login";
        itemService.closeItem(id, user);
        return "redirect:/items/my-items";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth.getName().equals("anonymousUser"))
            return null;
        return userRepository.findByEmailIgnoreCase(auth.getName().trim()).orElse(null);
    }
}
