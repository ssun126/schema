package com.dongwoo.SQM.auditMgmt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class NcrMgmtController {



    @GetMapping("/admin/auditMgmt/ncrMgmt")
    public String GetncrMgmtAdminList(Model model) {
        return "ncrMgmt/adminMain";
    }

    @GetMapping("/user/auditMgmt/ncrMgmt")
    public String GetncrMgmtList(Model model) {
        return "ncrMgmt/main";
    }

    @GetMapping("/user/auditMgmt/ncrMgmtdetail")
    public String GetncrMgmtAdminDetail(Model model) {
        return "ncrMgmt/detail";
    }


}
