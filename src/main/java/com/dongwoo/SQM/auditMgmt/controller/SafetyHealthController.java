package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.config.security.UserCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SafetyHealthController {
    @Autowired
    private AuditCommonService auditCommonService;

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
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();
        // 회사의 Auth 상태 정보를 가져옵니다.
        //AuditMgmtDTO companyAuth = auditCommonService.getCompanyAuthFile("SAFETY", comCode);
        //model.addAttribute("companyAuth", companyAuth);

        List<AuditMgmtDTO> companyAuthFile = auditCommonService.getCompanyAuthFile("SAFETY", comCode);
        model.addAttribute("companyAuthFile", companyAuthFile);
        return "safetyHealth/main";
    }
}
