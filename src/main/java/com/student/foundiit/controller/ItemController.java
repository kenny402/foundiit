package com.student.foundiit.controller;

import com.student.foundiit.model.Item;
import com.student.foundiit.model.User;
import com.student.foundiit.service.ItemService;
import com.student.foundiit.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping("/report")
    public String showReportForm(Model model) {
        model.addAttribute("item", new Item());
        return "report";
    }

    @PostMapping("/report")
    public String reportItem(@ModelAttribute("item") Item item,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        User user = userService.findByEmail(userDetails.getUsername());
        item.setUser(user);
        itemService.saveItem(item, file);
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String viewItem(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id);
        model.addAttribute("item", item);
        return "item-details";
    }
}
