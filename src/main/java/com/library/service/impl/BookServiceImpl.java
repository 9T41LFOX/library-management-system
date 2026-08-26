package com.library.service.impl;

import com.library.dto.BookForm;
import com.library.entity.Book;
import com.library.entity.BorrowStatus;
import com.library.entity.Category;
import com.library.entity.Supplier;
import com.library.exception.BusinessRuleException;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import com.library.repository.SupplierRepository;
import com.library.service.BookService;
import com.library.service.PdfStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final PdfStorageService pdfStorageService;

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> search(String keyword, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword, pageable);
    }

    @Override
    public Page<Book> findByCategory(Long categoryId, Pageable pageable) {
        return bookRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    public List<Book> findAvailableBooks() {
        return bookRepository.findByAvailableQuantityGreaterThan(0);
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Override
    @Transactional
    public Book save(BookForm form) {
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category not found with id: " + form.getCategoryId()));

        Set<Supplier> suppliers = new HashSet<>();
        if (form.getSupplierIds() != null && !form.getSupplierIds().isEmpty()) {
            suppliers = new HashSet<>(supplierRepository.findAllById(form.getSupplierIds()));
        }

        Book book;
        if (form.getId() != null) {
            book = findById(form.getId());
            if (form.getIsbn() != null && !form.getIsbn().equals(book.getIsbn())
                    && bookRepository.existsByIsbn(form.getIsbn())) {
                throw new DuplicateResourceException(
                        "ISBN '" + form.getIsbn() + "' is already assigned to another book");
            }
            int delta = form.getQuantity() - book.getQuantity();
            int newAvailable = Math.max(0, Math.min(form.getQuantity(), book.getAvailableQuantity() + delta));
            book.setAvailableQuantity(newAvailable);
        } else {
            if (form.getIsbn() != null && bookRepository.existsByIsbn(form.getIsbn())) {
                throw new DuplicateResourceException(
                        "ISBN '" + form.getIsbn() + "' is already assigned to another book");
            }
            book = new Book();
            book.setAvailableQuantity(form.getQuantity());
        }

        book.setTitle(form.getTitle());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        book.setQuantity(form.getQuantity());
        book.setCategory(category);
        book.setSuppliers(suppliers);

        // Only touch the PDF when a new file was actually submitted, so
        // editing a book without re-uploading a file keeps the existing one.
        if (form.getPdfFile() != null && !form.getPdfFile().isEmpty()) {
            String storedFileName = pdfStorageService.store(form.getPdfFile());
            pdfStorageService.delete(book.getPdfFileName());
            book.setPdfFileName(storedFileName);
        }

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        boolean hasActiveBorrow = book.getBorrows().stream()
                .anyMatch(b -> b.getStatus() == BorrowStatus.ISSUED);
        if (hasActiveBorrow) {
            throw new BusinessRuleException("Cannot delete a book that is currently issued to a member");
        }
        pdfStorageService.delete(book.getPdfFileName());
        bookRepository.delete(book);
    }

    @Override
    public long countTotalBooks() {
        return bookRepository.sumTotalQuantity();
    }

    @Override
    public long countAvailableBooks() {
        return bookRepository.sumAvailableQuantity();
    }
}