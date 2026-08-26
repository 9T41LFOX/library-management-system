package com.library.controller;

import com.library.entity.Book;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Read-only, member-facing book catalog used for the online PDF reader.
 *
 * This is intentionally a separate controller from BookController, which
 * stays the ADMIN-only management screen under /admin/books. Nothing here
 * matches the /admin/** or /user/** patterns in SecurityConfig, so it falls
 * through to the default ".anyRequest().authenticated()" rule - any signed
 * in user (ADMIN or USER) can browse and read, while anonymous visitors are
 * redirected to /login, matching the feature's access requirements.
 */
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookCatalogController {

    private final BookService bookService;

    @GetMapping
    public String catalog(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by("title"));
        Page<Book> books = (keyword != null && !keyword.isBlank())
                ? bookService.search(keyword, pageable)
                : bookService.findAll(pageable);
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        return "books/catalog";
    }

    @GetMapping("/{id}/read")
    public String read(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "books/read";
    }
}
