package com.dongwoo.SQM.system.login.controller;

import com.dongwoo.SQM.system.login.dto.LoginDTO;
import com.dongwoo.SQM.system.login.service.LoginService;
import com.dongwoo.SQM.system.menu.dto.MenuDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm() {
        return "/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDTO loginDTO, HttpSession session, Model model) {
        boolean isSuccess = false;
        List<HashMap> storedObjectList = null;
        List<HashMap> storedLanguageList = null;
        List<MenuDTO> storedMenuList = null;

        LoginDTO loginResult = loginService.login(loginDTO);
        if (loginResult != null) {
            // login 성공
            session.setAttribute("loginId", loginResult.getUSER_Id());
            session.setAttribute("loginName", loginResult.getUSER_Name());

            isSuccess = true;
            //메뉴 권한 가져오기
            if(!loginResult.getUSER_Id().isEmpty()){
                storedMenuList = loginService.getMenuList(loginDTO);
                model.addAttribute("storedMenuList", storedMenuList);
            }

            return "main";
        } else {
            // login 실패
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
