package com.library.repository;

import com.library.entity.Borrow;
import com.library.entity.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    int countByMemberIdAndStatus(Long memberId, BorrowStatus status);

    List<Borrow> findByMemberIdAndStatus(Long memberId, BorrowStatus status);

    Page<Borrow> findByMemberIdOrderByIssueDateDesc(Long memberId, Pageable pageable);

    Page<Borrow> findByStatus(BorrowStatus status, Pageable pageable);

    Page<Borrow> findByBook_TitleContainingIgnoreCaseOrMember_NameContainingIgnoreCase(
            String bookTitle, String memberName, Pageable pageable);

    long countByStatus(BorrowStatus status);

    long countByIssueDate(LocalDate issueDate);

    long countByReturnDate(LocalDate returnDate);

    @Query("SELECT COALESCE(SUM(b.fine), 0) FROM Borrow b")
    BigDecimal sumAllFines();

    List<Borrow> findTop10ByOrderByIdDesc();

    List<Borrow> findByStatusAndFineGreaterThan(BorrowStatus status, BigDecimal amount);

    @Query("SELECT b FROM Borrow b WHERE b.status = com.library.entity.BorrowStatus.ISSUED AND b.dueDate < :today")
    List<Borrow> findOverdueBorrows(@Param("today") LocalDate today);
}