package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import com.dongwoo.SQM.config.security.UserCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ISOAuthController {
    private final IsoAuthService isoAuthService;

    @GetMapping("/user/auditMgmt/isoAuth")
    public String isoAuthMain(Model model, @AuthenticationPrincipal UserCustom user) {
        // 회사의 ISO 상태 정보를 가져옵니다.
        AuditMgmtDTO companyAuth = isoAuthService.getCompanyAuth("ISO", user.getCOM_CODE());
        model.addAttribute("companyIsoAuth", companyAuth);
        return "isoAuth/main";
    }

    @GetMapping("/admin/auditMgmt/isoAuth")
    public String isoAuthAdminMain(Model model) {
        return "isoAuth/adminMain";
    }

    @GetMapping("/admin/auditMgmt/isoDetail")
    public String isoAuthAdminDetail(Model model, @RequestParam("COM_CODE") String com_code) {
        // 회사의 ISO 상태 정보를 가져옵니다.
        AuditMgmtDTO companyIsoAuth = isoAuthService.getCompanyAuth("ISO", com_code);
        model.addAttribute("companyAuth", companyIsoAuth);
        return "isoAuth/detail";
    }


}
