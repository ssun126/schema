package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.service.AuditResultService;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import com.dongwoo.SQM.config.security.UserCustom;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuditResultController {
    @Autowired
    private AuditResultService auditResultService;

    @GetMapping("/admin/auditMgmt/auditResult")
    public String AuditResultMain(Model model, HttpServletRequest req) {

        return "auditResult/adminMain";
    }

    @GetMapping("/user/auditMgmt/auditResult")
    public String AuditResultUserMain(Model model, HttpServletRequest req, @AuthenticationPrincipal UserCustom user) {
        String name = "";
        String code = user.getCOM_CODE(); //접속된 업체의 업체코드
        String state = "";
        String type = "";
        List<AuditMgmtDTO>  auditRsltData = auditResultService.searchAudit(type, code, name, state);
        model.addAttribute("auditRsltData", auditRsltData);
        return "auditResult/main";
    }
}
