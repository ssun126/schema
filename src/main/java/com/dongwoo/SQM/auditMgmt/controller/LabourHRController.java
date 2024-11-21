package com.dongwoo.SQM.auditMgmt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LabourHRController {
    @GetMapping("/admin/auditMgmt/labourHR")
    public String labourHRMain(Model model) {
        return "labourHR/main";
    }

    @GetMapping("/admin/auditMgmt/labourHRDetail")
    public String labourHRDetail(Model model) {
        return "labourHR/detail";
    }
}
