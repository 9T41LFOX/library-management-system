package com.library.controller;

import com.library.entity.Borrow;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final BorrowService borrowService;

    @Value("${library.fine.per-day}")
    private BigDecimal finePerDay;

    @GetMapping
    public String index() {
        return "reports/index";
    }

    @GetMapping("/overdue")
    public String overdueReport(Model model) {
        List<Borrow> overdue = borrowService.findOverdue();
        LocalDate today = LocalDate.now();

        Map<Long, BigDecimal> estimatedFines = new HashMap<>();
        Map<Long, Long> daysLate = new HashMap<>();
        for (Borrow b : overdue) {
            long days = ChronoUnit.DAYS.between(b.getDueDate(), today);
            estimatedFines.put(b.getId(), finePerDay.multiply(BigDecimal.valueOf(days)));
            daysLate.put(b.getId(), days);
        }

        model.addAttribute("overdueBorrows", overdue);
        model.addAttribute("estimatedFines", estimatedFines);
        model.addAttribute("daysLate", daysLate);
        model.addAttribute("today", today);
        return "reports/overdue";
    }

    @GetMapping("/fines")
    public String fineReport(Model model) {
        List<Borrow> fined = borrowService.findFinedBorrows();
        BigDecimal total = fined.stream()
                .map(Borrow::getFine)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("borrows", fined);
        model.addAttribute("totalFines", total);
        return "reports/fines";
    }
}