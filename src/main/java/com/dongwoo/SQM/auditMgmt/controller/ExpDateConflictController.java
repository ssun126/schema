package com.dongwoo.SQM.auditMgmt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ExpDateConflictController {
    @GetMapping("/admin/auditMgmt/expDateConflict")
    public String isoAuthMain(Model model) {
        return "expDateConflict/main";
    }
}
