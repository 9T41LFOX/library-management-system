package com.library.service;

import com.library.dto.BookForm;
import com.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    Page<Book> findAll(Pageable pageable);

    Page<Book> search(String keyword, Pageable pageable);

    Page<Book> findByCategory(Long categoryId, Pageable pageable);

    List<Book> findAvailableBooks();

    Book findById(Long id);

    Book save(BookForm form);

    void delete(Long id);

    long countTotalBooks();

    long countAvailableBooks();
}
