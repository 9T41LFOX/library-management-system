package com.library.dto;

import com.library.entity.Borrow;
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
public class UserDashboardStats {
    private List<Borrow> borrowedBooks;
    private long returnedBooksCount;
    private BigDecimal currentFine;
}
