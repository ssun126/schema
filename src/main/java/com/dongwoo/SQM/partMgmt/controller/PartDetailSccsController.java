package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
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
public class PartDetailSccsController {
    private final PartDetailService partDetailService;
    private final PartMgmtService partMgmtService;


    ///partMgmtSubmit  승인요청
    @PostMapping("/partMgmtSubmit")
    public String partMgmtSubmit(@ModelAttribute partDetailSccsDTO sccsDTO
                                , @ModelAttribute partDetailIngredDTO ingredDTO
                                , @ModelAttribute partDetailGuarantDTO guarantDTO
                                , @RequestParam("SCCS_FILE") MultipartFile sccsFile
                                , @RequestParam("INGRED_FILE") MultipartFile ingredFile
                                , @RequestParam(value="GUARANT_FILE",required = false) MultipartFile guarantFile
                                , @AuthenticationPrincipal UserCustom user
                                , Model model){

        String pm_idx = sccsDTO.getPM_IDX();
        String flag = sccsDTO.getINFO_FLAG();
        log.info("test5455555555========999999==========="+sccsDTO);
        if(!sccsDTO.getSCCS_CONFIRM_DATE().equals("") || !sccsDTO.getSCCS_CHAR().equals("") || !sccsFile.isEmpty() ){
            if(!sccsFile.isEmpty()){
                String sccs_filepath = partDetailService.uploadFileData(user.getCOM_CODE(),sccsFile);
                String sccs_filename = sccsFile.getOriginalFilename();

                sccsDTO.setSCCS_FILE_NAME(sccs_filename);
                sccsDTO.setSCCS_FILE_PATH(sccs_filepath);

            }
            partDetailService.saveSccsData(sccsDTO);

        }
        log.info("test5455555555========000000000===========");

        if(!ingredDTO.getINGRED_CONFIRM_DATE().equals("") || !ingredFile.isEmpty() ){
            if(!ingredFile.isEmpty()){
                String ingred_filepath = partDetailService.uploadFileData(user.getCOM_CODE(),ingredFile);
                String ingred_filename = ingredFile.getOriginalFilename();

                ingredDTO.setINGRED_FILE_PATH(ingred_filepath);
                ingredDTO.setINGRED_FILE_NAME(ingred_filename);

            }
            partDetailService.saveIngredData(ingredDTO);

        }
        log.info("test5455555555======11111=============");
        if(guarantDTO != null){
            if(!guarantDTO.getGUARANT_CONFIRM_DATE().equals("") || !guarantDTO.getGUARANT_TYPE().equals("") ||!guarantFile.isEmpty() ){
                if(!ingredFile.isEmpty()){
                    String guarant_filepath = partDetailService.uploadFileData(user.getCOM_CODE(),guarantFile);
                    String guarant_filename = ingredFile.getOriginalFilename();

                    guarantDTO.setGUARANT_FILE_PATH(guarant_filepath);
                    guarantDTO.setGUARANT_FILE_NAME(guarant_filename);

                }
                partDetailService.saveGuarantData(guarantDTO);

            }
        }

        log.info("test5455555555==========2222=========");

        //승인요청 pm_idx 상태 바꿈
        if(flag.equals("next")){
            //partMgmtService.updateApprovalStatus(pm_idx,"3");
        }


        //바인딩 리스트
        //검색 basecode 취급플랜트
        List<HashMap> searchPlantList = partMgmtService.getPlantList();
        //검색 basecode 승인현황
        //List<HashMap> searhApprovalStatus = partMgmtService.getApprovalStatus();

        model.addAttribute("searchPlantList",searchPlantList);
        //model.addAttribute("searhApprovalStatus", searhApprovalStatus);
        log.info("test5455555555===================");

        return "partMgmtList/main";
    }
}
