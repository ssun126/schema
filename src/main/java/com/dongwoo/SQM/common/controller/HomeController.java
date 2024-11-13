package com.dongwoo.SQM.common.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null) {
            // 인증되지 않은 사용자는 로그인 페이지로 리디렉션
            return "redirect:/login";
        } else {
            // 인증된 사용자는 대시보드 페이지로 리디렉션
            return "main";
        }
    }

    @GetMapping("/index")
    public String indexPage() {
        return "index";
    }

    //페이지 생성 전 링크 연결
    @GetMapping("/admin/partMgmt/approvalState")
    public String partMgmtMatApproval() {
        return "matApproval/main";
    }

    @GetMapping("/user/partMgmt/matReg")
    public String partMgmtMatReg() {
        return "matReg/main";
    }

    @GetMapping("/admin/qualityCtrl/changeMgmt")
    public String qualityCtrlMain() {
        return "qualityCtrl/main";
    }
}
