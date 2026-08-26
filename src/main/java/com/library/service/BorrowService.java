package com.library.service;

import com.library.entity.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BorrowService {
    Borrow issueBook(Long memberId, Long bookId);

    Borrow returnBook(Long borrowId);

    Page<Borrow> findAll(Pageable pageable);

    Page<Borrow> search(String keyword, Pageable pageable);

    List<Borrow> findActiveByMember(Long memberId);

    Page<Borrow> findHistoryByMember(Long memberId, Pageable pageable);

    List<Borrow> findOverdue();

    List<Borrow> findRecentActivity();

    List<Borrow> findAllActive();

    List<Borrow> findFinedBorrows();
}
