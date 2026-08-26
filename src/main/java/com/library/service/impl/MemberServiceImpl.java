package com.library.service.impl;

import com.library.dto.MemberForm;
import com.library.entity.BorrowStatus;
import com.library.entity.Member;
import com.library.exception.BusinessRuleException;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.MemberRepository;
import com.library.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @Override
    public Page<Member> search(String keyword, Pageable pageable) {
        return memberRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public List<Member> findAllList() {
        return memberRepository.findAll();
    }

    @Override
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    @Override
    public Member findByUserId(Long userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No member profile linked to this account"));
    }

    @Override
    @Transactional
    public Member save(MemberForm form) {
        Member member;
        if (form.getId() != null) {
            member = findById(form.getId());
            memberRepository.findByEmail(form.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(form.getId())) {
                    throw new DuplicateResourceException("Email '" + form.getEmail() + "' is already in use");
                }
            });
        } else {
            if (memberRepository.existsByEmail(form.getEmail())) {
                throw new DuplicateResourceException("Email '" + form.getEmail() + "' is already in use");
            }
            member = new Member();
            member.setJoinDate(LocalDate.now());
        }
        member.setName(form.getName());
        member.setPhone(form.getPhone());
        member.setEmail(form.getEmail());
        member.setAddress(form.getAddress());
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Member member = findById(id);
        boolean hasActiveBorrow = member.getBorrows().stream()
                .anyMatch(b -> b.getStatus() == BorrowStatus.ISSUED);
        if (hasActiveBorrow) {
            throw new BusinessRuleException("Cannot delete a member who currently has books checked out");
        }
        memberRepository.delete(member);
    }
}
