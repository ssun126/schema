package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditItemPointDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.LabourHRDTO;
import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.auditMgmt.service.LabourHRService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.LabourItemDTO;
import com.dongwoo.SQM.siteMgr.service.LabourItemService;
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
    private final LabourItemService labourItemService;
    private final AuditCommonService auditCommonService;

    @GetMapping("/admin/auditMgmt/labourHR")
    public String labourHRMain(Model model) {
        return "labourHR/adminMain";
    }

    @GetMapping("/admin/auditMgmt/labourHRDetail")
    public String labourHRDetail(Model model, @RequestParam("COM_CODE") String COM_CODE) {
        // 업체의 인증정보 가져오기 - 인증상태/인증일
        AuditMgmtDTO companyAuth = auditCommonService.getCompanyAuth("LABOUR", COM_CODE);
        model.addAttribute("companyAuth", companyAuth);

        // 회사의 노동환경 심사항목 정보를 가져옵니다.
        List<LabourItemDTO> auditItems = labourItemService.findAll();
        model.addAttribute("auditItems", auditItems);

        LabourHRDTO companyAuthFile = labourHRService.getCompanyAuthFile("LABOUR", COM_CODE);
        model.addAttribute("companyAuthFile", companyAuthFile);

        return "labourHR/detail";
    }

    @GetMapping("/user/auditMgmt/labourHR")
    public String labourHRUserMain(Model model) {

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();

        // 업체의 인증정보 가져오기 - 인증상태/인증일
        AuditMgmtDTO companyAuth = auditCommonService.getCompanyAuth("LABOUR", comCode);
        model.addAttribute("companyAuth", companyAuth);

        // 회사의 노동환경 심사항목 정보를 가져옵니다.
        List<AuditItemPointDTO> auditItemPoint = labourHRService.getCompanyAuthItemPoint("QUALITY", comCode);
        model.addAttribute("auditItemPoint", auditItemPoint);

        LabourHRDTO companyAuthFile = labourHRService.getCompanyAuthFile("LABOUR", comCode);
        model.addAttribute("companyAuthFile", companyAuthFile);
        log.info("companyAuthFile;;;;;;;;"+companyAuthFile);

        return "labourHR/main";
    }
}
