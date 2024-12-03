package com.dongwoo.SQM.companyInfo.controller;

import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoParamDTO;
import com.dongwoo.SQM.companyInfo.dto.PartCodeDTO;
import com.dongwoo.SQM.companyInfo.service.CompanyInfoService;
import com.dongwoo.SQM.companyInfo.service.PartCodeService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.system.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PartCodeController {

    private final CompanyInfoService companyInfoService;
    private final PartCodeService partCodeService;
    private final MemberService memberService;


    //자재등록   2024.11.11 sylee
    @GetMapping("/admin/companyInfo/partCodeList")
    public String cpMaterial(Model model) {
        List<BaseCodeDTO> deptList = partCodeService.GetBaseCode("CpWorkCode");
        model.addAttribute("deptList", deptList);

        return "partCodeMgmt/partCodeList";
    }



    //자재 등록 목록검색 LIST
    @PostMapping("/admin/companyInfo/getPartCodeList")
    public ResponseEntity<?> getcpMaterial(@RequestBody PartCodeDTO partCodeDTO ) {
        try {
            System.out.println("Received partCodeDTO: " + partCodeDTO);
            List<PartCodeDTO> partCodeList = partCodeService.partCodeList(partCodeDTO);
            return ResponseEntity.ok(partCodeList);
        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }


}
