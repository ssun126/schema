package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
@RequiredArgsConstructor
public class NcrMgmtController {

    @GetMapping("/admin/auditMgmt/ncrMgmt")
    public String getNcrMgmtAdminList(Model model) {
        return "ncrMgmt/adminMain";
    }

    @GetMapping("/admin/auditMgmt/ncrMgmtDetail")
    public String getNcrMgmtAdminDetail(Model model, @RequestParam("NCR_SEQ") String NCR_SEQ) {
        model.addAttribute("NCR_SEQ", NCR_SEQ);
        return "ncrMgmt/detail";
    }


    @GetMapping("/user/auditMgmt/ncrMgmt")
    public String getNcrMgmtList(Model model) {

        return "ncrMgmt/main";
    }

    @GetMapping("/user/auditMgmt/ncrMgmtDetail")
    public String getNcrMgmtDetail(Model model, @RequestParam("COM_CODE") String COM_CODE) {

        return "ncrMgmt/detail";
    }

}
