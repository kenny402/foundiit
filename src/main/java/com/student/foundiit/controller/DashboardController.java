package com.student.foundiit.controller;

import com.student.foundiit.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ItemService itemService;

    public DashboardController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("items", itemService.getAllItems());
        return "dashboard";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("items", itemService.getAllItems());
        return "index";
    }
}
