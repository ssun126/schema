package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.LabourHRDTO;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import com.dongwoo.SQM.auditMgmt.service.LabourHRService;
import com.dongwoo.SQM.config.security.UserCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LabourHRController {
    private final LabourHRService labourHRService;

    @GetMapping("/admin/auditMgmt/labourHR")
    public String labourHRMain(Model model) {
        return "labourHR/adminMain";
    }

    @GetMapping("/admin/auditMgmt/labourHRDetail")
    public String labourHRDetail(Model model, @RequestParam("COM_CODE") String COM_CODE) {
        log.info("aaaaaaaa"+COM_CODE);
        // 회사의 노동환경 상태 정보를 가져옵니다.
        //LabourHRDTO companyAuth = labourHRService.getCompanyAuth("LABOUR", COM_CODE);
        //model.addAttribute("companyAuth", companyAuth);

        LabourHRDTO companyAuthFile = labourHRService.getCompanyAuthFile("LABOUR", COM_CODE);
        model.addAttribute("companyAuthFile", companyAuthFile);
        log.info("bbbbbbbbbbbbbbb"+companyAuthFile);

        return "labourHR/detail";
    }

    @GetMapping("/user/auditMgmt/labourHR")
    public String labourHRUserMain(Model model) {

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();

        // 회사의 노동환경 상태 정보를 가져옵니다.
        //AuditMgmtDTO companyAuth = labourHRService.getCompanyAuth("LABOUR", comCode);
        //model.addAttribute("companyAuth", companyAuth);

        LabourHRDTO companyAuthFile = labourHRService.getCompanyAuthFile("LABOUR", comCode);
        model.addAttribute("companyAuthFile", companyAuthFile);

        return "labourHR/main";
    }
}
