package com.library.controller;

import com.library.entity.Member;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.service.DashboardService;
import com.library.service.MemberService;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final MemberService memberService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return user.getRole() == Role.ADMIN ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        model.addAttribute("stats", dashboardService.getAdminStats());
        return "dashboard/admin-dashboard";
    }

    @GetMapping("/user/dashboard")
    @PreAuthorize("hasRole('USER')")
    public String userDashboard(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Member member = memberService.findByUserId(user.getId());
        model.addAttribute("member", member);
        model.addAttribute("stats", dashboardService.getUserStats(member.getId()));
        return "dashboard/user-dashboard";
    }
}
