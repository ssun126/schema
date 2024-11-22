package com.dongwoo.SQM.auditMgmt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SafetyHealthController {
    @GetMapping("/admin/auditMgmt/safetyHealth")
    public String GetAdminList(Model model) {
        return "safetyHealth/adminMain";
    }

    @GetMapping("/admin/auditMgmt/safetyHealthDetail")
    public String labourHRDetail(Model model) {
        return "safetyHealth/detail";
    }

    @GetMapping("/user/auditMgmt/safetyHealth")
    public String GetList(Model model) {
        return "safetyHealth/main";
    }
}
