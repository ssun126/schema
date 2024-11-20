package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.partMgmt.dto.PartMgmtDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/partMgmt")
public class PartDetailMSDSController {

    //수정 버튼 시 이동 페이지
    @GetMapping("/goDetail")
    public String partMgmtGoDetail(@RequestParam("PM_IDX") String idx, Model model){
        model.addAttribute("PM_IDX",idx);
        return "partMgmtList/detail";
    }


}
