package com.dongwoo.SQM.auditMgmt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QualityControlController {
    @GetMapping("/admin/auditMgmt/qualityControl")
    public String GetList(Model model) {
        return "qualityControl/adminMain";
    }

    @GetMapping("/admin/auditMgmt/qualityControlDetail")
    public String GetDetail(Model model) {
        return "qualityControl/detail";
    }

    @GetMapping("/user/auditMgmt/qualityControl")
    public String GetUserList(Model model) {
        //업체의 품질관리 Audit 기본 정보를 보여준다.


        return "qualityControl/main";
    }
}
