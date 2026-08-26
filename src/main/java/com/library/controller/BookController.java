package com.library.controller;

import com.library.dto.BookForm;
import com.library.entity.Book;
import com.library.service.BookService;
import com.library.service.CategoryService;
import com.library.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("title"));
        Page<Book> books = (keyword != null && !keyword.isBlank())
                ? bookService.search(keyword, pageable)
                : bookService.findAll(pageable);
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        return "books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        addDropdowns(model);
        return "books/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setTitle(book.getTitle());
        form.setAuthor(book.getAuthor());
        form.setIsbn(book.getIsbn());
        form.setQuantity(book.getQuantity());
        form.setCategoryId(book.getCategory() != null ? book.getCategory().getId() : null);
        form.setSupplierIds(book.getSuppliers().stream().map(com.library.entity.Supplier::getId)
                .collect(Collectors.toList()));
        model.addAttribute("bookForm", form);
        model.addAttribute("book", book);
        addDropdowns(model);
        return "books/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("bookForm") BookForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            addDropdowns(model);
            return "books/form";
        }
        bookService.save(form);
        redirectAttributes.addFlashAttribute("success", "Book saved successfully");
        return "redirect:/admin/books";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Book deleted successfully");
        return "redirect:/admin/books";
    }

    private void addDropdowns(Model model) {
        model.addAttribute("categories", categoryService.findAllList());
        model.addAttribute("suppliers", supplierService.findAllList());
    }
}
