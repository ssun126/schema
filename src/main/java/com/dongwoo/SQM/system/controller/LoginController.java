package com.dongwoo.SQM.system.controller;

import com.dongwoo.SQM.system.dto.LoginDTO;
import com.dongwoo.SQM.system.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm() {
        return "/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDTO loginDTO, HttpSession session, Model model, Authentication authentication) {
        boolean isSuccess = false;
        log.info("PostMapping:::::::::::::::");
        LoginDTO loginResult = loginService.login(loginDTO);
        if (loginResult != null) {
            // login 성공
            session.setAttribute("loginId", loginResult.getUSER_ID());
            session.setAttribute("loginName", loginResult.getUSER_NAME());
            session.setAttribute("role", loginResult.getUSER_NAME());


            isSuccess = true;

            //log.info("authentication=="+authentication);
            //authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("authentication======"+authentication);

            // 권한 가져오기
            model.addAttribute("role", "admin");
            model.addAttribute("isSuccess", isSuccess);
            model.addAttribute("loginError", "");
            return "main";
        } else {
            // login 실패
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@ModelAttribute LoginDTO loginDTO, HttpSession session) {

        // login 세션 삭제
        String sessionId = (String) session.getAttribute("loginId");
        session.removeAttribute(sessionId);

        return "/login";
    }

    @GetMapping("/main")
    public String goMain() {
        return "main";
    }


}
