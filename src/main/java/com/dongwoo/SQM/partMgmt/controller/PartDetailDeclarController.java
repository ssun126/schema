package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
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
public class PartDetailDeclarController {

    private final PartDetailService partDetailService;
    private final PartMgmtService partMgmtService;

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
                                        , @AuthenticationPrincipal UserCustom user
                                        , Model model){

        String pm_idx = partDetailDeclarDTO.getPM_IDX();

        partDetailService.saveDetailDeclarData(partDetailDeclarDTO,declarfile,user.getCOM_CODE());


        if(partDetailDeclarDTO.getINFO_FLAG().equals("save")){
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

            //sccs 성분 rlxk 들고가기
            partDetailSccsDTO sccsDTO = partDetailService.getSccsData(pm_idx);
            if(sccsDTO == null) sccsDTO = new partDetailSccsDTO();
            model.addAttribute("sccsDTO",sccsDTO);
            log.info("sccsDTO=============================" + sccsDTO);

            partDetailIngredDTO ingredDTO = partDetailService.getIngredData(pm_idx);
            if(ingredDTO == null) ingredDTO = new partDetailIngredDTO();
            model.addAttribute("ingredDTO",ingredDTO);
            log.info("ingredDTO=============================" + ingredDTO);

            //보증
            List<partDetailGuarantDTO> guarantListDTO =partDetailService.getGuarantData(pm_idx);
            if(guarantListDTO == null) {
                partDetailGuarantDTO guarantDTO = new partDetailGuarantDTO();
                guarantListDTO.add(guarantDTO);
            }
            model.addAttribute("guarantListDTO",guarantListDTO);
            log.info("guarantListDTO=============================" + guarantListDTO);

            return "partMgmtList/sccsDetail";
        }
    }

}
