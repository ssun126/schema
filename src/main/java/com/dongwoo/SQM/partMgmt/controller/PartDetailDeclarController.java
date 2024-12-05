package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.partMgmt.dto.PartDetailSvhcDTO;
import com.dongwoo.SQM.partMgmt.dto.partDetailDeclarDTO;
import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/partMgmt")
public class PartDetailDeclarController {

    private final PartDetailService partDetailService;

    @GetMapping("/getDeclarData")
    public @ResponseBody List<DeclarationDTO> getDeclarData(){
        List<DeclarationDTO> declarList = partDetailService.getDeclarData();
        log.info("test=3232222-================="+declarList);
        return declarList;
    }

    //다음 버튼 클릭 시 저장 후 다음 페이지로 이동
    @PostMapping("/goSccs")
    public String partMgmtGoDeclaration(@ModelAttribute partDetailDeclarDTO partDetailDeclarDTO
            , @RequestParam("DECLAR_FILE") MultipartFile declarfile
            , Model model){

        String pm_idx = partDetailDeclarDTO.getPM_IDX();


        model.addAttribute("PM_IDX",pm_idx);
        return "partMgmtList/sccsDetail";
    }

}
