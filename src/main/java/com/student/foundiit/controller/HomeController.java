package com.student.foundiit.controller;

import com.student.foundiit.model.ItemStatus;
import com.student.foundiit.repository.CategoryRepository;
import com.student.foundiit.service.ItemService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ItemService itemService;
    private final CategoryRepository categoryRepository;

    public HomeController(ItemService itemService, CategoryRepository categoryRepository) {
        this.itemService = itemService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("recentLost", itemService.getItemsByStatus(ItemStatus.LOST, PageRequest.of(0, 6)).getContent());
        model.addAttribute("recentFound", itemService.getItemsByStatus(ItemStatus.FOUND, PageRequest.of(0, 6)).getContent());
        model.addAttribute("categories", categoryRepository.findAllByOrderByCategoryNameAsc());
        model.addAttribute("totalLost", itemService.countByStatus(ItemStatus.LOST));
        model.addAttribute("totalFound", itemService.countByStatus(ItemStatus.FOUND));
        return "home/index";
    }
}
