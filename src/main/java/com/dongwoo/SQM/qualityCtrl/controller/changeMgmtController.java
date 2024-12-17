package com.dongwoo.SQM.qualityCtrl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class changeMgmtController {
    @GetMapping("/admin/qualityCtrl/changeMgmt")
    public String getAdminChangeMgmtList(Model model) {
        return "changeMgmt/adminMain";
    }

    @GetMapping("/user/qualityCtrl/changeMgmt")
    public String getChangeMgmtList(Model model) {
        return "changeMgmt/main";
    }
}

