package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStats {
    private long totalBooks;
    private long availableBooks;
    private long borrowedBooks;
    private long totalMembers;
    private long totalSuppliers;
    private long totalCategories;
    private long todayBorrowCount;
    private long todayReturnCount;
    private BigDecimal totalFineCollected;
    private List<RecentActivityDto> recentActivities;
}
