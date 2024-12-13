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
public class QualityControlController {
    @Autowired
    private AuditCommonService auditCommonService;

    @GetMapping("/admin/auditMgmt/qualityControl")
    public String GetList(Model model) {
        return "qualityControl/adminMain";
    }

    @GetMapping("/admin/auditMgmt/qualityControlDetail")
    public String GetDetail(Model model) {
        return "qualityControl/detail";
    }

    @GetMapping("/user/auditMgmt/qualityControl")
    public String GetUserList(Model model) {
        //업체의 품질관리 Audit 기본 정보를 보여준다.
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();

        List<AuditMgmtDTO> companyAuthFile = auditCommonService.getCompanyAuthFile("QUALITY", comCode);
        model.addAttribute("companyAuthFile", companyAuthFile);

        return "qualityControl/main";
    }
}
