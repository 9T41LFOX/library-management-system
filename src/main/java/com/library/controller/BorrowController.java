package com.library.controller;

import com.library.entity.Borrow;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/borrow")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BorrowController {

    private final BorrowService borrowService;
    private final MemberService memberService;
    private final BookService bookService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Borrow> borrows = (keyword != null && !keyword.isBlank())
                ? borrowService.search(keyword, pageable)
                : borrowService.findAll(pageable);
        model.addAttribute("borrows", borrows);
        model.addAttribute("keyword", keyword);
        return "borrow/list";
    }

    @GetMapping("/issue")
    public String issueForm(Model model) {
        model.addAttribute("members", memberService.findAllList());
        model.addAttribute("books", bookService.findAvailableBooks());
        return "borrow/issue";
    }

    @PostMapping("/issue")
    public String issue(@RequestParam Long memberId,
            @RequestParam Long bookId,
            RedirectAttributes redirectAttributes) {
        borrowService.issueBook(memberId, bookId);
        redirectAttributes.addFlashAttribute("success", "Book issued successfully");
        return "redirect:/admin/borrow";
    }
}
