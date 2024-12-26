package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.ConflictMineralsDTO;
import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.auditMgmt.service.ConflictMineralsService;
import com.dongwoo.SQM.config.security.UserCustom;
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
public class ConflictMineralsController {
    @Autowired
    private AuditCommonService auditCommonService;
    @Autowired
    private ConflictMineralsService conflictMineralsService;

    @GetMapping("/admin/auditMgmt/conflictMinerals")
    public String AdminMain(Model model) {
        return "conflictMinerals/adminMain";
    }

    @GetMapping("/admin/auditMgmt/conflictMineralsDetail")
    public String Detail(Model model, @RequestParam("COM_CODE") String COM_CODE) {
        // 회사의 ISO 상태 정보를 가져옵니다.
        AuditMgmtDTO companyAuth = auditCommonService.getCompanyAuth("CONFLICT", COM_CODE);
        model.addAttribute("companyAuth", companyAuth);
        //분쟁광물정보 가져오기
        List<ConflictMineralsDTO> conflictData = conflictMineralsService.getConflictData("CONFLICT", COM_CODE);
        model.addAttribute("conflictData", conflictData);

        //첨부 파일 가져오기
        List<AuditMgmtDTO> companyAuthFile = auditCommonService.getCompanyAuthFile("CONFLICT", COM_CODE);
        model.addAttribute("companyAuthFile", companyAuthFile);

        return "conflictMinerals/detail";
    }

    @GetMapping("/user/auditMgmt/conflictMinerals")
    public String Main(Model model) {
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();
        // 업체의 인증정보 가져오기 - 인증상태/인증일
        AuditMgmtDTO companyAuth = auditCommonService.getCompanyAuth("CONFLICT", comCode);
        model.addAttribute("companyAuth", companyAuth);

        //분쟁광물정보 가져오기
        List<ConflictMineralsDTO> conflictData = conflictMineralsService.getConflictData("CONFLICT", comCode);
        model.addAttribute("conflictData", conflictData);

        //첨부 파일 가져오기
        List<AuditMgmtDTO> companyAuthFile = auditCommonService.getCompanyAuthFile("CONFLICT", comCode);
        model.addAttribute("companyAuthFile", companyAuthFile);

        return "conflictMinerals/main";
    }
}
