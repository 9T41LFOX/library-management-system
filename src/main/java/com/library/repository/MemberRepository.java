package com.library.repository;

import com.library.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(Long userId);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Member> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
