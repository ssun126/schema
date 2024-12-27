package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditItemPointDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.auditMgmt.service.SafetyHealthService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.QualityItemDTO;
import com.dongwoo.SQM.siteMgr.dto.SafetyItemDTO;
import com.dongwoo.SQM.siteMgr.service.SafetyItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SafetyHealthController {
    @Autowired
    private AuditCommonService auditCommonService;
    private final SafetyHealthService safetyHealthService;

    @GetMapping("/admin/auditMgmt/safetyHealth")
    public String GetAdminList(Model model) {
        return "safetyHealth/adminMain";
    }

    @GetMapping("/admin/auditMgmt/safetyHealthDetail")
    public String labourHRDetail(Model model, @RequestParam("COM_CODE") String COM_CODE) {

        // 업체의 인증정보 가져오기 - 인증상태/인증일
        AuditMgmtDTO companyAuth = auditCommonService.getCompanyAuth("SAFETY", COM_CODE);
        model.addAttribute("companyAuth", companyAuth);

        // 회사의  심사항목 정보를 가져옵니다.
        List<AuditItemPointDTO> auditItemPoint = safetyHealthService.getCompanyAuthItemPoint("SAFETY", COM_CODE);
        for (AuditItemPointDTO dto: auditItemPoint){
            String originAudit = dto.getAUDIT_CRITERIA();
            String originPoint = dto.getPOINT_CRITERIA();
            String formattedAudit = originAudit != null ? originAudit.replace("\n", "<br>") : "";
            String formattedPoint = originPoint != null ? originPoint.replace("\n", "<br>") : "";
            dto.setAUDIT_CRITERIA(formattedAudit);
            dto.setPOINT_CRITERIA(formattedPoint);
        }
        model.addAttribute("auditItemPoint", auditItemPoint);

        List<AuditMgmtDTO> companyAuthFile = auditCommonService.getCompanyAuthFile("SAFETY", COM_CODE);
        model.addAttribute("companyAuthFile", companyAuthFile);


        return "safetyHealth/detail";
    }

    @GetMapping("/user/auditMgmt/safetyHealth")
    public String GetList(Model model) {
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();

        // 업체의 인증정보 가져오기 - 인증상태/인증일
        AuditMgmtDTO companyAuth = auditCommonService.getCompanyAuth("SAFETY", comCode);
        model.addAttribute("companyAuth", companyAuth);

        //저장된 데이터가 있는지 확인
        List<AuditItemPointDTO> auditItemPoint = safetyHealthService.getCompanyAuthItemPoint("SAFETY", comCode);
        for (AuditItemPointDTO dto: auditItemPoint){
            String originAudit = dto.getAUDIT_CRITERIA();
            String originPoint = dto.getPOINT_CRITERIA();
            String formattedAudit = originAudit != null ? originAudit.replace("\n", "<br>") : "";
            String formattedPoint = originPoint != null ? originPoint.replace("\n", "<br>") : "";
            dto.setAUDIT_CRITERIA(formattedAudit);
            dto.setPOINT_CRITERIA(formattedPoint);
        }
        model.addAttribute("auditItemPoint", auditItemPoint);

        List<AuditMgmtDTO> companyAuthFile = auditCommonService.getCompanyAuthFile("SAFETY", comCode);
        model.addAttribute("companyAuthFile", companyAuthFile);
        return "safetyHealth/main";
    }
}
