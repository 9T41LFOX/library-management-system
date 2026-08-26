package com.library.controller;

import com.library.dto.MemberForm;
import com.library.dto.RegistrationForm;
import com.library.entity.Member;
import com.library.entity.User;
import com.library.service.MemberService;
import com.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MemberService memberService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        MemberForm memberForm = new MemberForm();
        memberForm.setName(form.getName());
        memberForm.setPhone(form.getPhone());
        memberForm.setEmail(form.getEmail());
        memberForm.setAddress(form.getAddress());

        userService.registerMember(memberForm, form.getUsername(), form.getPassword());
        redirectAttributes.addFlashAttribute("success", "Registration successful - please log in");
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Member member = memberService.findByUserId(user.getId());
        MemberForm form = new MemberForm();
        form.setId(member.getId());
        form.setName(member.getName());
        form.setPhone(member.getPhone());
        form.setEmail(member.getEmail());
        form.setAddress(member.getAddress());
        model.addAttribute("memberForm", form);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("memberForm") MemberForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile";
        }
        memberService.save(form);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        return "redirect:/profile";
    }
}
