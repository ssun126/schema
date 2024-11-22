package com.dongwoo.SQM.auditMgmt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuditResultController {
    @GetMapping("/admin/auditMgmt/auditResult")
    public String AuditResultMain(Model model) {
        return "auditResult/adminMain";
    }

    @GetMapping("/user/auditMgmt/auditResult")
    public String AuditResultUserMain(Model model) {
        return "auditResult/main";
    }
}
