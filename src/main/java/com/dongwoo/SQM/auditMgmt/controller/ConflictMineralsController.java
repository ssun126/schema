package com.dongwoo.SQM.auditMgmt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConflictMineralsController {
    @GetMapping("/admin/auditMgmt/conflictMinerals")
    public String AdminMain(Model model) {
        return "conflictMinerals/adminMain";
    }

    @GetMapping("/admin/auditMgmt/conflictMinerals/detail")
    public String Detail(Model model) {
        return "conflictMinerals/detail";
    }

    @GetMapping("user/auditMgmt/conflictMinerals")
    public String Main(Model model) {
        return "conflictMinerals/main";
    }
}
