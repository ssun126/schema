package com.dongwoo.SQM.partMgmt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminPartMgmtController {
    @GetMapping("/admin/partMgmt/approvalState")
    public String PartMgmtList(Model model) {
        return "approvalState/adminMain";
    }

    @GetMapping("/admin/partMgmt/approvalStateDetail")
    public String PartMgmtDetail(Model model) {
        return "approvalState/detail";
    }
}
