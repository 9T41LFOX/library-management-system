package com.library.controller;

import com.library.entity.Borrow;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/return")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReturnController {

    private final BorrowService borrowService;

    @GetMapping
    public String activeList(Model model) {
        model.addAttribute("borrows", borrowService.findAllActive());
        model.addAttribute("today", LocalDate.now());
        return "borrow/return";
    }

    @PostMapping("/{id}")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Borrow borrow = borrowService.returnBook(id);
        String message = borrow.getFine().signum() > 0
                ? "Book returned. Fine charged: $" + borrow.getFine()
                : "Book returned on time - no fine";
        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:/admin/return";
    }
}
