package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.partMgmt.dto.PartDetailSvhcDTO;
import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/partMgmt")
public class PartDetailSvhcController {

    private final PartDetailService partDetailService;

    @GetMapping("/getSvhcListData")
    public @ResponseBody  List<SvhcListDTO> getSvhcData(){
        log.info("test=-11111=================");
        List<SvhcListDTO> svhcList = partDetailService.getSvhcData();
        log.info("test=-11111================="+svhcList);

        return svhcList;
    }


    //다음 버튼 클릭 시 저장 후 다음 페이지로 이동
    @PostMapping("/goDeclaration")
    public String partMgmtGoDeclaration(@ModelAttribute PartDetailSvhcDTO partDetailSvhcDTO
                                        , @RequestParam("SVHC_FILE") MultipartFile svhcfile
                                        , Model model){

        String pm_idx = partDetailSvhcDTO.getPM_IDX();


        model.addAttribute("PM_IDX",pm_idx);
        return "partMgmtList/declarationDetail";
    }
}
