package com.library.config;

import com.library.entity.Member;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.MemberRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
        }

        // Demo member account, since the project's registration page isn't part
        // of this change set - this gives a ready-made USER login to test the
        // member-facing book catalog and PDF reader with.
        if (!userRepository.existsByUsername("member")) {
            User memberUser = new User();
            memberUser.setUsername("member");
            memberUser.setPassword(passwordEncoder.encode("member123"));
            memberUser.setRole(Role.USER);
            memberUser.setEnabled(true);
            memberUser = userRepository.save(memberUser);

            Member member = new Member();
            member.setUser(memberUser);
            member.setName("Demo Member");
            member.setEmail("demo.member@libraryhub.test");
            member.setJoinDate(LocalDate.now());
            memberRepository.save(member);
        }
    }
}