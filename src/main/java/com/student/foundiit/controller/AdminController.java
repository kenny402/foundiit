package com.student.foundiit.controller;

import com.student.foundiit.model.Item;
import com.student.foundiit.model.ItemStatus;
import com.student.foundiit.model.Role;
import com.student.foundiit.model.User;
import com.student.foundiit.service.ClaimService;
import com.student.foundiit.service.ItemService;
import com.student.foundiit.service.MatchService;
import com.student.foundiit.service.UserService;
import com.student.foundiit.model.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_admin')")
public class AdminController {

    private final UserService userService;
    private final ItemService itemService;
    private final MatchService matchService;
    private final ClaimService claimService;

    public AdminController(UserService userService, ItemService itemService,
                           MatchService matchService, ClaimService claimService) {
        this.userService = userService;
        this.itemService = itemService;
        this.matchService = matchService;
        this.claimService = claimService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers",
            userService.getAllUsers(PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements());
        model.addAttribute("totalItems",
            itemService.countByStatus(ItemStatus.LOST) +
            itemService.countByStatus(ItemStatus.FOUND));
        model.addAttribute("totalMatches", matchService.countTotalMatches());
        model.addAttribute("pendingClaims", claimService.countByStatus(ClaimStatus.PENDING));
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false) String keyword,
                        Model model) {
        Page<User> userPage;
        if (keyword != null && !keyword.isEmpty()) {
            userPage = userService.searchUsers(keyword, PageRequest.of(page, 10));
        } else {
            userPage = userService.getAllUsers(PageRequest.of(page, 10));
        }
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam Role role) {
        userService.updateRole(id, role);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id) {
        userService.toggleEnabled(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/items")
    public String allItems(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Item> itemPage = itemService.getAllActiveItems(PageRequest.of(page, 10));
        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        return "admin/items";
    }

    @PostMapping("/items/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return "redirect:/admin/items";
    }

    @GetMapping("/reports")
    public String reports() {
        return "admin/reports";
    }

    @GetMapping("/reports/export/excel")
    @ResponseBody
    public void exportExcel(jakarta.servlet.http.HttpServletResponse response)
            throws java.io.IOException {
        response.setContentType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=FoundItPlus_Report.xlsx");
        try (org.apache.poi.ss.usermodel.Workbook wb =
                new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Items");
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Title");
            header.createCell(2).setCellValue("Status");
            header.createCell(3).setCellValue("Location");
            java.util.List<Item> items =
                itemService.getAllActiveItems(PageRequest.of(0, 1000)).getContent();
            int rowNum = 1;
            for (Item item : items) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(item.getTitle());
                row.createCell(2).setCellValue(item.getStatus().name());
                row.createCell(3).setCellValue(item.getLocation());
            }
            wb.write(response.getOutputStream());
        }
    }

    @GetMapping("/reports/export/pdf")
    @ResponseBody
    public void exportPdf(jakarta.servlet.http.HttpServletResponse response)
            throws java.io.IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=FoundItPlus_Report.pdf");
        com.lowagie.text.Document doc =
            new com.lowagie.text.Document(com.lowagie.text.PageSize.A4);
        com.lowagie.text.pdf.PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();
        com.lowagie.text.Font font =
            com.lowagie.text.FontFactory.getFont(
                com.lowagie.text.FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        com.lowagie.text.Paragraph title =
            new com.lowagie.text.Paragraph("FoundIt+ Report", font);
        title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(new com.lowagie.text.Paragraph(" "));
        com.lowagie.text.pdf.PdfPTable table =
            new com.lowagie.text.pdf.PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell("ID"); table.addCell("Title");
        table.addCell("Status"); table.addCell("Date");
        java.util.List<Item> items =
            itemService.getAllActiveItems(PageRequest.of(0, 50)).getContent();
        for (Item item : items) {
            table.addCell(String.valueOf(item.getId()));
            table.addCell(item.getTitle());
            table.addCell(item.getStatus().name());
            table.addCell(item.getDateCreated() != null ?
                item.getDateCreated().toString() : "N/A");
        }
        doc.add(table);
        doc.close();
    }
}