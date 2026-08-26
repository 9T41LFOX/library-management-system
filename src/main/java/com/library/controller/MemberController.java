package com.library.controller;

import com.library.dto.MemberForm;
import com.library.entity.Member;
import com.library.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("name"));
        Page<Member> members = (keyword != null && !keyword.isBlank())
                ? memberService.search(keyword, pageable)
                : memberService.findAll(pageable);
        model.addAttribute("members", members);
        model.addAttribute("keyword", keyword);
        return "members/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("member", memberService.findById(id));
        return "members/view";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Member member = memberService.findById(id);
        MemberForm form = new MemberForm();
        form.setId(member.getId());
        form.setName(member.getName());
        form.setPhone(member.getPhone());
        form.setEmail(member.getEmail());
        form.setAddress(member.getAddress());
        model.addAttribute("memberForm", form);
        return "members/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("memberForm") MemberForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "members/form";
        }
        memberService.save(form);
        redirectAttributes.addFlashAttribute("success", "Member saved successfully");
        return "redirect:/admin/members";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        memberService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Member deleted successfully");
        return "redirect:/admin/members";
    }
}
