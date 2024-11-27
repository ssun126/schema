package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.partMgmt.dto.partDetailMsdsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/partMgmt")
public class PartDetailSvhcController {


    //다음 버튼 클릭 시 저장 후 다음 페이지로 이동
    @PostMapping("/goDeclaration")
    public String partMgmtGoDeclaration(@ModelAttribute partDetailMsdsDTO partDetailMsdsDTO){

        return "partMgmtList/declarationDetail";
    }
}
