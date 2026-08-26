package com.library.service;

import com.library.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    Page<Category> findAll(Pageable pageable);

    Page<Category> search(String keyword, Pageable pageable);

    List<Category> findAllList();

    Category findById(Long id);

    Category save(Category category);

    void delete(Long id);
}
