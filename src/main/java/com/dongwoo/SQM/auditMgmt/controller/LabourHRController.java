package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LabourHRController {
    private final IsoAuthService isoAuthService;

    @GetMapping("/admin/auditMgmt/labourHR")
    public String labourHRMain(Model model) {
        return "labourHR/adminMain";
    }

    @GetMapping("/admin/auditMgmt/labourHRDetail")
    public String labourHRDetail(Model model, @RequestParam("COM_CODE") String com_code) {
        // 회사의 노동인권 상태 정보를 가져옵니다.
        AuditMgmtDTO companyAuth = isoAuthService.getCompanyAuth("LABOUR", com_code);
        model.addAttribute("companyAuth", companyAuth);

        return "labourHR/detail";
    }

    @GetMapping("/user/auditMgmt/labourHR")
    public String labourHRUserMain(Model model) {
        return "labourHR/main";
    }
}
