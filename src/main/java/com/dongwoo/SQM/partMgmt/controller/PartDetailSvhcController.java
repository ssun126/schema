package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.partMgmt.dto.PartDetailSvhcDTO;
import com.dongwoo.SQM.partMgmt.dto.PartMgmtDTO;
import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import com.dongwoo.SQM.partMgmt.dto.partDetailDeclarDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final PartMgmtService partMgmtService;

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
                                        , @AuthenticationPrincipal UserCustom user
                                        , Model model){

        String pm_idx = partDetailSvhcDTO.getPM_IDX();

        partDetailService.saveDetailSvhcData(partDetailSvhcDTO,svhcfile,user.getCOM_CODE());

        if(partDetailSvhcDTO.getINFO_FLAG().equals("save")){
            //목록

            List<HashMap> searchPlantList = partMgmtService.getPlantList();
            //검색 basecode 승인현황
            //List<HashMap> searhApprovalStatus = partMgmtService.getApprovalStatus();

            model.addAttribute("searchPlantList",searchPlantList);
            //model.addAttribute("searhApprovalStatus", searhApprovalStatus);

            return "partMgmtList/main";
        }else{
            model.addAttribute("PM_IDX",pm_idx);
            PartMgmtDTO partMgmtDTO = partDetailService.getPartData(pm_idx);
            if(partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
            model.addAttribute("partMgmtDTO",partMgmtDTO);
            log.info("partMgmtDTO=============================" + partMgmtDTO);
            //svhc data 들고가기
            partDetailDeclarDTO  declarDTO = partDetailService.getDetailDeclarData(pm_idx);
            if(declarDTO == null) declarDTO = new partDetailDeclarDTO();
            model.addAttribute("declarDTO",declarDTO);
            return "partMgmtList/declarationDetail";
        }


    }
}
