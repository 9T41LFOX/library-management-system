package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String title, String author, Pageable pageable);

    Page<Book> findByCategoryId(Long categoryId, Pageable pageable);

    List<Book> findByAvailableQuantityGreaterThan(int quantity);

    boolean existsByIsbn(String isbn);

    @Query("SELECT COALESCE(SUM(b.quantity), 0) FROM Book b")
    long sumTotalQuantity();

    @Query("SELECT COALESCE(SUM(b.availableQuantity), 0) FROM Book b")
    long sumAvailableQuantity();
}
