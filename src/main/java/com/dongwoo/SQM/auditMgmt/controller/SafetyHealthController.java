package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SafetyHealthController {
    @GetMapping("/admin/auditMgmt/safetyHealth")
    public String GetAdminList(Model model) {
        return "safetyHealth/adminMain";
    }

    @GetMapping("/admin/auditMgmt/safetyHealthDetail")
    public String labourHRDetail(Model model) {
        return "safetyHealth/detail";
    }

    @GetMapping("/user/auditMgmt/safetyHealth")
    public String GetList(Model model) {
        // 회사의 ISO 상태 정보를 가져옵니다.
        //AuditMgmtDTO companyIsoAuth = isoAuthService.getCompanyAuth("ISO", com_code);
        //model.addAttribute("companyAuth", companyIsoAuth);
        return "safetyHealth/main";
    }
}
