package com.dongwoo.SQM.auditMgmt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class conflictMineralsController {
    @GetMapping("/admin/auditMgmt/conflictMinerals")
    public String Main(Model model) {
        return "conflictMinerals/main";
    }

    @GetMapping("/admin/auditMgmt/conflictMinerals/detail")
    public String Detail(Model model) {
        return "conflictMinerals/detail";
    }
}