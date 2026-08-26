package com.library.service.impl;

import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.entity.BorrowStatus;
import com.library.entity.Member;
import com.library.exception.BusinessRuleException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.repository.MemberRepository;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Value("${library.borrow.max-books}")
    private int maxBooks;

    @Value("${library.borrow.period-days}")
    private int borrowPeriodDays;

    @Value("${library.fine.per-day}")
    private BigDecimal finePerDay;

    @Override
    @Transactional
    public Borrow issueBook(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        int activeCount = borrowRepository.countByMemberIdAndStatus(memberId, BorrowStatus.ISSUED);
        if (activeCount >= maxBooks) {
            throw new BusinessRuleException(
                    member.getName() + " has already borrowed the maximum of " + maxBooks + " books");
        }
        if (book.getAvailableQuantity() <= 0) {
            throw new BusinessRuleException("No available copies of \"" + book.getTitle() + "\" right now");
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        Borrow borrow = new Borrow();
        borrow.setMember(member);
        borrow.setBook(book);
        borrow.setIssueDate(today);
        borrow.setDueDate(today.plusDays(borrowPeriodDays));
        borrow.setStatus(BorrowStatus.ISSUED);
        borrow.setFine(BigDecimal.ZERO);

        return borrowRepository.save(borrow);
    }

    @Override
    @Transactional
    public Borrow returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with id: " + borrowId));

        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new BusinessRuleException("This book has already been returned");
        }

        LocalDate today = LocalDate.now();
        long lateDays = Math.max(0, ChronoUnit.DAYS.between(borrow.getDueDate(), today));
        BigDecimal fine = finePerDay.multiply(BigDecimal.valueOf(lateDays));

        borrow.setReturnDate(today);
        borrow.setFine(fine);
        borrow.setStatus(BorrowStatus.RETURNED);

        Book book = borrow.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);

        return borrowRepository.save(borrow);
    }

    @Override
    public Page<Borrow> findAll(Pageable pageable) {
        return borrowRepository.findAll(pageable);
    }

    @Override
    public Page<Borrow> search(String keyword, Pageable pageable) {
        return borrowRepository.findByBook_TitleContainingIgnoreCaseOrMember_NameContainingIgnoreCase(
                keyword, keyword, pageable);
    }

    @Override
    public List<Borrow> findActiveByMember(Long memberId) {
        return borrowRepository.findByMemberIdAndStatus(memberId, BorrowStatus.ISSUED);
    }

    @Override
    public Page<Borrow> findHistoryByMember(Long memberId, Pageable pageable) {
        return borrowRepository.findByMemberIdOrderByIssueDateDesc(memberId, pageable);
    }

    @Override
    public List<Borrow> findOverdue() {
        return borrowRepository.findOverdueBorrows(LocalDate.now());
    }

    @Override
    public List<Borrow> findRecentActivity() {
        return borrowRepository.findTop10ByOrderByIdDesc();
    }

    @Override
    public List<Borrow> findAllActive() {
        return borrowRepository.findByStatus(BorrowStatus.ISSUED, Pageable.unpaged()).getContent();
    }

    @Override
    public List<Borrow> findFinedBorrows() {
        return borrowRepository.findByStatusAndFineGreaterThan(BorrowStatus.RETURNED, BigDecimal.ZERO);
    }
}
