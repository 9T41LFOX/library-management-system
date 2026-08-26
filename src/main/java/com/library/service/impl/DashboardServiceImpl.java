package com.library.service.impl;

import com.library.dto.AdminDashboardStats;
import com.library.dto.RecentActivityDto;
import com.library.dto.UserDashboardStats;
import com.library.entity.Borrow;
import com.library.entity.BorrowStatus;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.repository.CategoryRepository;
import com.library.repository.MemberRepository;
import com.library.repository.SupplierRepository;
import com.library.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;
    private final MemberRepository memberRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;

    @Value("${library.fine.per-day}")
    private BigDecimal finePerDay;

    @Override
    public AdminDashboardStats getAdminStats() {
        LocalDate today = LocalDate.now();

        List<RecentActivityDto> recentActivities = borrowRepository.findTop10ByOrderByIdDesc().stream()
                .map(this::toActivity)
                .toList();

        return AdminDashboardStats.builder()
                .totalBooks(bookRepository.sumTotalQuantity())
                .availableBooks(bookRepository.sumAvailableQuantity())
                .borrowedBooks(borrowRepository.countByStatus(BorrowStatus.ISSUED))
                .totalMembers(memberRepository.count())
                .totalSuppliers(supplierRepository.count())
                .totalCategories(categoryRepository.count())
                .todayBorrowCount(borrowRepository.countByIssueDate(today))
                .todayReturnCount(borrowRepository.countByReturnDate(today))
                .totalFineCollected(borrowRepository.sumAllFines())
                .recentActivities(recentActivities)
                .build();
    }

    @Override
    public UserDashboardStats getUserStats(Long memberId) {
        List<Borrow> active = borrowRepository.findByMemberIdAndStatus(memberId, BorrowStatus.ISSUED);
        long returnedCount = borrowRepository.countByMemberIdAndStatus(memberId, BorrowStatus.RETURNED);

        LocalDate today = LocalDate.now();
        BigDecimal currentFine = active.stream()
                .map(b -> {
                    long lateDays = Math.max(0, ChronoUnit.DAYS.between(b.getDueDate(), today));
                    return finePerDay.multiply(BigDecimal.valueOf(lateDays));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return UserDashboardStats.builder()
                .borrowedBooks(active)
                .returnedBooksCount(returnedCount)
                .currentFine(currentFine)
                .build();
    }

    private RecentActivityDto toActivity(Borrow borrow) {
        String description = borrow.getStatus() == BorrowStatus.RETURNED
                ? borrow.getMember().getName() + " returned \"" + borrow.getBook().getTitle() + "\""
                : borrow.getMember().getName() + " borrowed \"" + borrow.getBook().getTitle() + "\"";
        LocalDate date = borrow.getStatus() == BorrowStatus.RETURNED ? borrow.getReturnDate() : borrow.getIssueDate();
        return new RecentActivityDto(description, date);
    }
}
