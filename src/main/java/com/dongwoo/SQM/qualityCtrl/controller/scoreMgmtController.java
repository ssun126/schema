package com.dongwoo.SQM.qualityCtrl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class scoreMgmtController {
    @GetMapping("/admin/qualityCtrl/scoreMgmt")
    public String isoAuthMain(Model model) {
        return "scoreMgmt/main";
    }
}
