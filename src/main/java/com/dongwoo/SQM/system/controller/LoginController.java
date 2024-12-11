package com.dongwoo.SQM.system.controller;

import com.dongwoo.SQM.common.util.JWTSecretKeyUtils;
import com.dongwoo.SQM.system.dto.LoginDTO;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.system.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@RequestParam(value="error", required = false)String error,
                            @RequestParam(value="exception", required = false)String exception,
                            Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "/login";
    }

    @PostMapping("/auth/login")
    public String login(@ModelAttribute LoginDTO loginDTO, HttpSession session, Model model, Authentication authentication) {
        boolean isSuccess = false;
        log.info("PostMapping:::::::::::::::"+loginDTO);

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
            return "login";
        }
    }

    @PostMapping("/findDW")
    public ResponseEntity<?> findDW(HttpServletRequest req) {
        try {
            String DWPCID = req.getParameter("DWPCID");
            String DWEMail = req.getParameter("DWEMail");

            Map<String, Object> DW_INFO = loginService.findDW(DWPCID, DWEMail);

            if (DW_INFO.size() == 0) {
                return ResponseEntity.ok("정보가 없습니다.");
            } else {
                int USER_IDX = Integer.parseInt(DW_INFO.get("USER_IDX").toString());

                // 기존 비밀번호 신규 생성
                JWTSecretKeyUtils.refreshSecretKey256();
                String NewPass = JWTSecretKeyUtils.getSecretKey256().toString().substring(3);
                log.info("findDW - NewPass:::::::::::::::"+NewPass);
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode(NewPass);

                // 신규 생성 업데이트
                loginService.updateUserPWD(USER_IDX, encodedPassword);

                // 메일 발송
                
                return ResponseEntity.ok("OK");
            }
        } catch (Exception e) {
            log.info("findDW === "+e.getMessage());
            return ResponseEntity.ok("오류가 발생하였습니다.");
        }
    }

    @PostMapping("/findCompanyID")
    public ResponseEntity<?> findCompanyID(HttpServletRequest req) {
        String CompanySearchIDName = req.getParameter("CompanySearchIDName");
        String CompanySearchIDEmail = req.getParameter("CompanySearchIDEmail");

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/findCompanyPW")
    public ResponseEntity<?> findCompanyPW(HttpServletRequest req) {
        String CompanySearchPWID = req.getParameter("CompanySearchPWID");
        String CompanySearchPWName = req.getParameter("CompanySearchPWName");
        String CompanySearchPWEmail = req.getParameter("CompanySearchPWEmail");

        return ResponseEntity.ok("OK");
    }

    @GetMapping("/logout")
    public String logout(@ModelAttribute LoginDTO loginDTO, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication3======"+auth);
        // login 세션 삭제
        String sessionId = (String) session.getAttribute("loginId");
        session.removeAttribute(sessionId);

        return "/login";
    }

    @GetMapping("/main")
    public String goMain(@AuthenticationPrincipal UserCustom user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication4======"+auth);
        log.info("user======"+user);

        if(auth.isAuthenticated()){
            // login 성공
            log.info("login======"+user.getUSER_NAME());
            for (GrantedAuthority authority : auth.getAuthorities()) {
                log.info(authority.getAuthority());
            }
        }

        return "main";
    }


}
