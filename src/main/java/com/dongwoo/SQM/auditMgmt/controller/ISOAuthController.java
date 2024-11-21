package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ISOAuthController {
    private final IsoAuthService isoAuthService;

    @GetMapping("/user/auditMgmt/isoAuth")
    public String isoAuthMain(Model model) {
        return "isoAuth/main";
    }

    @GetMapping("/admin/auditMgmt/isoAuth")
    public String isoAuthAdminMain(Model model) {
        return "isoAuth/adminMain";
    }

    @GetMapping("/admin/auditMgmt/isoDetail")
    public String isoAuthAdminDetail(Model model) {
        return "isoAuth/detail";
    }


}
