package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuditResultController {
    @Autowired
    private IsoAuthService isoAuthService;

    @GetMapping("/admin/auditMgmt/auditResult")
    public String AuditResultMain(Model model, HttpServletRequest req) {
        String name = req.getParameter("searchName");
        String code = req.getParameter("searchCode");
        String state = req.getParameter("searchState");
        String type = req.getParameter("searchType");
        log.info("type??"+type);

        List<AuditMgmtDTO>  auditMgmtDTO = isoAuthService.searchCompanies(type, code, name, state);
        return "auditResult/adminMain";
    }

    @GetMapping("/user/auditMgmt/auditResult")
    public String AuditResultUserMain(Model model) {
        return "auditResult/main";
    }
}
