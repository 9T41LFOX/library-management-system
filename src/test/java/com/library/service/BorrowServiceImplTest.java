package com.library.service;

import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.entity.BorrowStatus;
import com.library.entity.Member;
import com.library.exception.BusinessRuleException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.repository.MemberRepository;
import com.library.service.impl.BorrowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowServiceImplTest {

    @Mock
    private BorrowRepository borrowRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(borrowService, "maxBooks", 3);
        ReflectionTestUtils.setField(borrowService, "borrowPeriodDays", 15);
        ReflectionTestUtils.setField(borrowService, "finePerDay", new BigDecimal("2.00"));

        member = new Member();
        member.setId(1L);
        member.setName("Jane Doe");

        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAvailableQuantity(1);
    }

    @Test
    void issueBookFailsWhenMemberAlreadyHasThreeBooks() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowRepository.countByMemberIdAndStatus(1L, BorrowStatus.ISSUED)).thenReturn(3);

        assertThrows(BusinessRuleException.class, () -> borrowService.issueBook(1L, 1L));
    }

    @Test
    void issueBookFailsWhenNoCopiesAvailable() {
        book.setAvailableQuantity(0);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowRepository.countByMemberIdAndStatus(1L, BorrowStatus.ISSUED)).thenReturn(0);

        assertThrows(BusinessRuleException.class, () -> borrowService.issueBook(1L, 1L));
    }

    @Test
    void issueBookSucceedsAndSetsDueDateFifteenDaysOut() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowRepository.countByMemberIdAndStatus(1L, BorrowStatus.ISSUED)).thenReturn(0);
        when(borrowRepository.save(any(Borrow.class))).thenAnswer(inv -> inv.getArgument(0));

        Borrow result = borrowService.issueBook(1L, 1L);

        assertThat(result.getDueDate()).isEqualTo(result.getIssueDate().plusDays(15));
        assertThat(result.getStatus()).isEqualTo(BorrowStatus.ISSUED);
        assertThat(book.getAvailableQuantity()).isEqualTo(0);
    }

    @Test
    void returnBookChargesTwoDollarsPerDayLate() {
        Borrow borrow = new Borrow();
        borrow.setId(1L);
        borrow.setBook(book);
        borrow.setMember(member);
        borrow.setStatus(BorrowStatus.ISSUED);
        borrow.setIssueDate(LocalDate.now().minusDays(20));
        borrow.setDueDate(LocalDate.now().minusDays(5));

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));
        when(borrowRepository.save(any(Borrow.class))).thenAnswer(inv -> inv.getArgument(0));

        Borrow result = borrowService.returnBook(1L);

        assertThat(result.getFine()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(result.getStatus()).isEqualTo(BorrowStatus.RETURNED);
    }

    @Test
    void returnBookFailsWhenAlreadyReturned() {
        Borrow borrow = new Borrow();
        borrow.setId(1L);
        borrow.setStatus(BorrowStatus.RETURNED);

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));

        assertThrows(BusinessRuleException.class, () -> borrowService.returnBook(1L));
    }
}
