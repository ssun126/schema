package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditItemPointDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.auditMgmt.service.QualityControlService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.QualityItemDTO;
import com.dongwoo.SQM.siteMgr.service.QualityItemService;
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
public class QualityControlController {
    @Autowired
    private AuditCommonService auditCommonService;
    @Autowired
    private QualityItemService qualityItemService;
    @Autowired
    private QualityControlService qualityControlService;

    @GetMapping("/admin/auditMgmt/qualityControl")
    public String GetList(Model model) {
        return "qualityControl/adminMain";
    }

    @GetMapping("/admin/auditMgmt/qualityControlDetail")
    public String GetDetail(Model model, @RequestParam("COM_CODE") String COM_CODE) {

        List<QualityItemDTO> auditItems = qualityItemService.findAll();
        model.addAttribute("auditItems", auditItems);

        List<AuditMgmtDTO> companyAuthFile = auditCommonService.getCompanyAuthFile("QUALITY", COM_CODE);
        model.addAttribute("companyAuthFile", companyAuthFile);

        return "qualityControl/detail";
    }

    @GetMapping("/user/auditMgmt/qualityControl")
    public String GetUserList(Model model) {
        //업체의 품질관리 Audit 기본 정보를 보여준다.
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();

        //저장된 데이터가 있는지 확인
        List<AuditItemPointDTO> auditItemPoint = qualityControlService.getCompanyAuthItemPoint("QUALITY", comCode);
        model.addAttribute("auditItemPoint", auditItemPoint);

        List<AuditMgmtDTO> companyAuthFile = auditCommonService.getCompanyAuthFile("QUALITY", comCode);
        model.addAttribute("companyAuthFile", companyAuthFile);

        return "qualityControl/main";
    }
}
