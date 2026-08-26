package com.library.service;

import com.library.dto.MemberForm;
import com.library.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService {
    Page<Member> findAll(Pageable pageable);

    Page<Member> search(String keyword, Pageable pageable);

    List<Member> findAllList();

    Member findById(Long id);

    Member findByUserId(Long userId);

    Member save(MemberForm form);

    void delete(Long id);
}
