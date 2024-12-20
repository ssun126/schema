package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.repository.CompanyInfoRepository;
import com.dongwoo.SQM.companyInfo.service.CompanyInfoService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ISOAuthController {
    private final IsoAuthService isoAuthService;
    private final CompanyInfoService companyInfoService;

    @GetMapping("/user/auditMgmt/isoAuth")
    public String isoAuthMain(Model model, Criteria criteria,  @AuthenticationPrincipal UserCustom user) {

        // 회사의 ISO 상태 정보를 가져옵니다.
        AuditMgmtDTO companyAuth = isoAuthService.getCompanyAuth("ISO", user.getCOM_CODE());
        model.addAttribute("companyAuth", companyAuth);

        // 회사의 ISO 항목 정보를 가져옵니다.
        List<IsoAuthItemDTO> companyIsoAuth = isoAuthService.getList(user.getCOM_CODE());
        model.addAttribute("companyIsoAuth", companyIsoAuth);

        return "isoAuth/main";
    }

    @GetMapping("/admin/auditMgmt/isoAuth")
    public String isoAuthAdminMain(Model model) {
        return "isoAuth/adminMain";
    }

    @GetMapping("/admin/auditMgmt/isoDetail")
    public String isoAuthAdminDetail(Model model, @RequestParam("COM_CODE") String COM_CODE) {

        // 회사의 ISO 상태 정보를 가져옵니다.
        AuditMgmtDTO companyAuth = isoAuthService.getCompanyAuth("ISO", COM_CODE);
        model.addAttribute("companyAuth", companyAuth);

        // 회사의 ISO 항목 정보를 가져옵니다.
        List<IsoAuthItemDTO> companyIsoAuth = isoAuthService.getList(COM_CODE);
        model.addAttribute("companyIsoAuth", companyIsoAuth);
        return "isoAuth/detail";
    }


}
