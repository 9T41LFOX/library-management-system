package com.library.repository;

import com.library.entity.Book;
import com.library.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void sumTotalAndAvailableQuantityAcrossMultipleBooks() {
        Category category = new Category();
        category.setName("Programming");
        category = categoryRepository.save(category);

        Book book1 = new Book();
        book1.setTitle("Clean Code");
        book1.setAuthor("Robert C. Martin");
        book1.setQuantity(3);
        book1.setAvailableQuantity(2);
        book1.setCategory(category);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Effective Java");
        book2.setAuthor("Joshua Bloch");
        book2.setQuantity(5);
        book2.setAvailableQuantity(5);
        book2.setCategory(category);
        bookRepository.save(book2);

        assertThat(bookRepository.sumTotalQuantity()).isEqualTo(8);
        assertThat(bookRepository.sumAvailableQuantity()).isEqualTo(7);
    }

    @Test
    void existsByIsbnDetectsDuplicates() {
        Category category = new Category();
        category.setName("Fiction");
        category = categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("Dune");
        book.setAuthor("Frank Herbert");
        book.setIsbn("978-0441013593");
        book.setQuantity(1);
        book.setAvailableQuantity(1);
        book.setCategory(category);
        bookRepository.save(book);

        assertThat(bookRepository.existsByIsbn("978-0441013593")).isTrue();
        assertThat(bookRepository.existsByIsbn("000-0000000000")).isFalse();
    }
}
