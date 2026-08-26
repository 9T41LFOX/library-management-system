package com.library.service.impl;

import com.library.entity.Supplier;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.SupplierRepository;
import com.library.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public Page<Supplier> findAll(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }

    @Override
    public Page<Supplier> search(String keyword, Pageable pageable) {
        return supplierRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public List<Supplier> findAllList() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
    }

    @Override
    @Transactional
    public Supplier save(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        supplierRepository.delete(findById(id));
    }
}
