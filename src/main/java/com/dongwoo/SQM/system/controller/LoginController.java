package com.dongwoo.SQM.system.controller;

import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.common.util.JWTSecretKeyUtils;
import com.dongwoo.SQM.config.security.FormWebAuthenticationDetails;
import com.dongwoo.SQM.system.dto.LoginDTO;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.system.dto.UserInfoCompanyUserDTO;
import com.dongwoo.SQM.system.service.LoginService;
import com.dongwoo.SQM.system.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    private final MemberService memberService;

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
        FormWebAuthenticationDetails details = ( FormWebAuthenticationDetails ) authentication.getDetails();
        int comUserIdx = details.getComUserIdx();
        log.info("comUserIdx======"+comUserIdx);

        LoginDTO loginResult = loginService.login(loginDTO);
        if (loginResult != null) {
            if (loginResult.getUSER_GUBN().equals("1")) {
                // OTP 인증번호 체크
                String otpNumOk = session.getAttribute("otpNumOk").toString();

                if (!otpNumOk.equals("Y")) {
                    // login 실패
                    model.addAttribute("loginError", "OTP 인증을 해주세요.");
                    return "login";
                }
            }
            
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
            model.addAttribute("role", "ROLE_ADMIN");
            model.addAttribute("isSuccess", isSuccess);
            model.addAttribute("loginError", "");
            return "main";
        } else {
            // login 실패
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "login";
        }
    }

    @PostMapping("/idpwCheck")
    public ResponseEntity<?> idpwCheck(HttpServletRequest req, HttpSession session) {
        try {
            String username = req.getParameter("username").strip();
            String password = req.getParameter("password").strip();

            PasswordEncoder encoder = new BCryptPasswordEncoder();

            LoginDTO loginDTO = loginService.findById(username);
            log.info("getUSER_PWD === "+loginDTO.getUSER_PWD());
            boolean isPasswordMatches = encoder.matches(password, loginDTO.getUSER_PWD());

            if (isPasswordMatches) {
                session.setAttribute("okID", username);
                return ResponseEntity.ok("OK|" + loginDTO.getUSER_GUBN());
            } else {
                return ResponseEntity.ok("아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.");
            }
        } catch (Exception e) {
            log.info("idpwCheck === "+e.getMessage());
            return ResponseEntity.ok("오류가 발생하였습니다.");
        }
    }

    @PostMapping("/otp")
    public String otp(HttpServletRequest req, HttpServletResponse response, HttpSession session, Model model) {
        try {
            String username = req.getParameter("username");
            String okID = session.getAttribute("okID").toString();

            if (username.equals(okID)) {
                //공동 작업자
                UserInfoCompanyUserDTO parmaDTO = new UserInfoCompanyUserDTO();
                parmaDTO.setUSER_ID(username);  //username = USER_ID
                List<UserInfoCompanyUserDTO> companyUserList = memberService.findByMemberInfoAll(parmaDTO);
                model.addAttribute("companyUserList", companyUserList);
                log.info("companyUserList=="+ companyUserList);
            } else {
                PrintWriter printer = response.getWriter();
                printer.print("|||[ERROR]|||인증정보가 옳지 않습니다.");
                printer.close();
                return "blank";
            }


        } catch (Exception e) {
            log.info("otp === "+e.getMessage());
        }

        return "otp";
    }

    private String generateRandomOtp(int length) {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        // 6자리 난수 생성 (0부터 9까지의 숫자)
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }


    @PostMapping("/otpSend")
    public ResponseEntity<?> otpSend(HttpServletRequest req , HttpSession session) {
        try {
            int comUserIdx = Integer.parseInt(req.getParameter("COM_USER_IDX"));
            String username = req.getParameter("USER_NAME");

            //난수 6자리
            String randomOtp = generateRandomOtp(6);
            String sessionKey = comUserIdx + "_" + username;
            session.setAttribute(sessionKey, randomOtp);


            UserInfoCompanyUserDTO parmaDTO = new UserInfoCompanyUserDTO();
            parmaDTO.setCOM_USER_IDX(comUserIdx);
            List<UserInfoCompanyUserDTO> companyUserList = memberService.findByMemberInfoAll(parmaDTO);

            String email = "" ;
            for (UserInfoCompanyUserDTO user : companyUserList) {
                if (user.getCOM_USER_IDX() == comUserIdx) {
                    email = user.getUSER_EMAIL();
                }
            }

            //메일 발송.!! sendEmail(String recipientEmail, String subject, String content)
            log.info("OTP 메일 발송.!! === "+" email:"+email + " OTP:" + randomOtp);

            return ResponseEntity.ok("OK|" + randomOtp);

        } catch (Exception e) {
            log.info("otpSend === "+e.getMessage());
            return ResponseEntity.ok("오류가 발생하였습니다.");
        }
    }

    @PostMapping("/otpOk")
    public ResponseEntity<?> otpOk(HttpServletRequest req , HttpSession session) {
        try {

            String comUserIdx = req.getParameter("COM_USER_IDX");
            String username = req.getParameter("USER_NAME");
            String otpNum = req.getParameter("otpNum");

            String sessionKey = comUserIdx + "_" + username;
            String storedOtp = (String) session.getAttribute(sessionKey);


            if (storedOtp != null && storedOtp.equals(otpNum)) {
                log.info("OTP 인증 성공!! === " + true);
                return ResponseEntity.ok("OK|"+comUserIdx);
            } else {
                log.info("OTP 인증 실패!! === " + false);
                return ResponseEntity.ok("인증번호를 다시 확인해주세요.");
            }

        } catch (Exception e) {
            log.info("otpSend === "+e.getMessage());
            return ResponseEntity.ok("오류가 발생하였습니다.");
        }
    }

    @PostMapping("/findDW")
    public ResponseEntity<?> findDW(HttpServletRequest req) {
        try {
            String DWPCID = req.getParameter("DWPCID");
            String DWEMail = req.getParameter("DWEMail");

            Map<String, Object> DW_INFO = loginService.findDW(DWPCID, DWEMail);

            if (DW_INFO == null) {
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
        try {
            String CompanySearchIDName = req.getParameter("CompanySearchIDName");
            String CompanySearchIDEmail = req.getParameter("CompanySearchIDEmail");

            Map<String, Object> COM_INFO = loginService.findCompanyID(CompanySearchIDName, CompanySearchIDEmail);

            if (COM_INFO == null) {
                return ResponseEntity.ok("정보가 없습니다.");
            } else {
                int USER_IDX = Integer.parseInt(COM_INFO.get("USER_IDX").toString());
                String USER_ID = COM_INFO.get("USER_ID").toString();

                // 메일 발송
                log.info("findCompanyID - USER_ID:::::::::::::::"+USER_ID);

                return ResponseEntity.ok("OK");
            }
        } catch (Exception e) {
            log.info("findCompanyID === "+e.getMessage());
            return ResponseEntity.ok("오류가 발생하였습니다.");
        }
    }

    @PostMapping("/findCompanyPW")
    public ResponseEntity<?> findCompanyPW(HttpServletRequest req) {
        try {
            String CompanySearchPWID = req.getParameter("CompanySearchPWID");
            String CompanySearchPWName = req.getParameter("CompanySearchPWName");
            String CompanySearchPWEmail = req.getParameter("CompanySearchPWEmail");

            Map<String, Object> COM_INFO = loginService.findCompanyPW(CompanySearchPWID, CompanySearchPWName, CompanySearchPWEmail);

            if (COM_INFO == null) {
                return ResponseEntity.ok("정보가 없습니다.");
            } else {
                int USER_IDX = Integer.parseInt(COM_INFO.get("USER_IDX").toString());

                // 기존 비밀번호 신규 생성
                JWTSecretKeyUtils.refreshSecretKey256();
                String NewPass = JWTSecretKeyUtils.getSecretKey256().toString().substring(3);
                log.info("findCompanyPW - NewPass:::::::::::::::"+NewPass);
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode(NewPass);

                // 신규 생성 업데이트
                loginService.updateUserPWD(USER_IDX, encodedPassword);

                // 메일 발송

                return ResponseEntity.ok("OK");
            }
        } catch (Exception e) {
            log.info("findCompanyPW === "+e.getMessage());
            return ResponseEntity.ok("오류가 발생하였습니다.");
        }
    }

    @GetMapping("/logout")
    public String logout(@ModelAttribute LoginDTO loginDTO, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        FormWebAuthenticationDetails details = ( FormWebAuthenticationDetails ) auth.getDetails();
        int comUserIdx = details.getComUserIdx();
        log.info("comUserIdx======"+comUserIdx);
        // login 세션 삭제
        String sessionId = (String) session.getAttribute("loginId");
        session.removeAttribute(sessionId);

        return "/login";
    }

    @GetMapping("/main")
    public String goMain(@AuthenticationPrincipal UserCustom user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        FormWebAuthenticationDetails details = ( FormWebAuthenticationDetails ) auth.getDetails();
        int comUserIdx = details.getComUserIdx();
        log.info("authentication4======"+auth);

        if(auth.isAuthenticated()){
            // login 성공
            log.info("login======"+user.getUSER_NAME());
            log.info("comUserIdx======"+comUserIdx);


            log.info("user======"+user);
            for (GrantedAuthority authority : auth.getAuthorities()) {
                if(!authority.getAuthority().equals("ROLE_ADMIN")){
                    //공동 작업자  COM_USER_IDX 가져오기
                    UserInfoCompanyUserDTO parmaDTO = new UserInfoCompanyUserDTO();
                    parmaDTO.setCOM_CODE(user.getCOM_CODE()); //위에서 만들어진 사용자 IDX
                    parmaDTO.setCOM_USER_IDX(comUserIdx);
                    if(comUserIdx != 0) {
                        UserInfoCompanyUserDTO userInfoCompanyUserDTO = memberService.findByCompanyUserComUserIdx(parmaDTO);
                        String comUserName = userInfoCompanyUserDTO.getUSER_NAME();
                        if(!userInfoCompanyUserDTO.getUSER_NAME().isEmpty()) {
                            user.setCOM_USER_IDX(comUserIdx);
                            user.setUSER_NAME(comUserName);
                        }
                    }
                }
                log.info(authority.getAuthority());
            }
        }

        return "main";
    }


}
