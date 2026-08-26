package com.library.service;

import com.library.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplierService {
    Page<Supplier> findAll(Pageable pageable);

    Page<Supplier> search(String keyword, Pageable pageable);

    List<Supplier> findAllList();

    Supplier findById(Long id);

    Supplier save(Supplier supplier);

    void delete(Long id);
}
