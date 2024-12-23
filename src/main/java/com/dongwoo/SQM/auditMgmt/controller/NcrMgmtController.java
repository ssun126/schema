package com.dongwoo.SQM.auditMgmt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

    @GetMapping("/user/auditMgmt/ncrMgmtDetail")
    public String GetncrMgmtAdminDetail(Model model, @RequestParam("COM_CODE") String COM_CODE) {
        return "ncrMgmt/detail";
    }


}
