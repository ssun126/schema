package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/partMgmt")
public class PartDetailSccsController {
    private final PartDetailService partDetailService;
    private final PartMgmtService partMgmtService;


    ///partMgmtSubmit  승인요청
    @GetMapping("/partMgmtSubmit")
    public String partMgmtSubmit(Model model){
        //바인딩 리스트
        //검색 basecode 취급플랜트
        List<HashMap> searchPlantList = partMgmtService.getPlantList();
        //검색 basecode 승인현황
        //List<HashMap> searhApprovalStatus = partMgmtService.getApprovalStatus();

        model.addAttribute("searchPlantList",searchPlantList);
        //model.addAttribute("searhApprovalStatus", searhApprovalStatus);

        return "partMgmtList/main";
    }
}
