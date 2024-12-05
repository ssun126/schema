package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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

    @GetMapping("/admin/partMgmt/expDateMsds")
    public String expDateMsdsList(Model model) {
        return "expDateMsds/main";
    }

    @GetMapping("/admin/partMgmt/expDateRohs")
    public String expDateRohsList(Model model) {
        return "expDateRohs/main";
    }

    @GetMapping("/admin/partMgmt/expDateEtc")
    public String expDateEtcList(Model model) {
        return "expDateEtc/main";
    }

    @GetMapping("/admin/partMgmt/expDateSvhc")
    public String expDateSvhcList(Model model) {
        return "expDateSvhc/main";
    }

    @GetMapping("/admin/partMgmt/expDateDecl")
    public String expDateDeclList(Model model) {
        return "expDateDecl/main";
    }







}
