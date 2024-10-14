package com.dongwoo.SQM.system.member.controller;

import com.dongwoo.SQM.system.member.dto.MemberDTO;
import com.dongwoo.SQM.system.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/agree")
    public String agreeForm(Model model) {
        return "/member/agree";
    }

    @GetMapping("/member/join")
    public String joinForm(Model model) {
        return "/member/join";
    }

    @GetMapping("/member/warranty")
    public String warrantyForm(Model model) {
        return "/member/warranty";
    }

    @GetMapping("/member/approve")
    public String approveForm(Model model) {
        return "/member/approve";
    }

    @GetMapping("/member/find")
    public String findForm(Model model) {
        return "/member/find";
    }

    @PostMapping("/member/save")
    public String save(@ModelAttribute MemberDTO memberDTO){
        memberService.save(memberDTO);

        return "/login";
    }

    @GetMapping("/member")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        // 어떠한 html로 가져갈 데이터가 있다면 model사용
        model.addAttribute("memberList", memberDTOList);
        return "/member/list";
    }

    @GetMapping("/member/{id}")
    public String findById(@PathVariable int id, Model model) {
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member", memberDTO);
        return "/member/detail";
    }

    @GetMapping("/member/myPage")
    public String myPage(HttpSession session, Model model) {
        // 세션에 저장된 나의 이메일 가져오기
        String loginId = (String) session.getAttribute("loginId");
        System.out.println("loginId????"+loginId);
        MemberDTO memberDTO = memberService.findByMemberId(loginId);
        model.addAttribute("member", memberDTO);
        return "/member/myPage";
    }

    @GetMapping("/member/update")
    public String updateForm(HttpSession session, Model model) {
        // 세션에 저장된 나의 이메일 가져오기
        String loginEmail = (String) session.getAttribute("loginEmail");
        System.out.println("loginEmail????"+loginEmail);
        MemberDTO memberDTO = memberService.findByMemberEmail(loginEmail);
        model.addAttribute("member", memberDTO);
        return "/member/update";
    }

    @PostMapping("/member/update")
    public String update(@ModelAttribute MemberDTO memberDTO) {
        System.out.println("memberDTO????"+memberDTO.getUSER_idx());
        boolean result = memberService.update(memberDTO);
        if (result) {
            return "redirect:/member?id=" + memberDTO.getUSER_idx();
        } else {
            return "index";
        }
    }

    @GetMapping("/member/delete/{id}")
    public String deleteById(@PathVariable int id) {
        memberService.delete(id);
        return "redirect:/member/";
    }

    @PostMapping("/member/email-check")
    public @ResponseBody String emailCheck(@RequestParam("memberEmail") String memberEmail) {
        System.out.println("memberEmail = " + memberEmail);
        String checkResult = memberService.emailCheck(memberEmail);
        return checkResult;
    }

}
