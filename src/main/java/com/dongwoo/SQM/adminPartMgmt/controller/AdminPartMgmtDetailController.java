package com.dongwoo.SQM.adminPartMgmt.controller;

import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminPartMgmtDetailController {

    private final PartDetailService partDetailService;

    @GetMapping("/admin/partMgmt/goReadDetail")
    public String goReadDetail(@RequestParam("PM_IDX") String idx, Model model){

        PartMgmtDTO partMgmtDTO = partDetailService.getPartData(idx);
        if(partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
        model.addAttribute("partMgmtDTO",partMgmtDTO);
        log.info("partMgmtDTO=============================" + partMgmtDTO);

        //데이터가 있다면 들고와서 뿌려줌
        //MSDS
        partDetailMsdsDTO msdsDTO = partDetailService.getMsdsData(idx);
        if(msdsDTO == null) msdsDTO = new partDetailMsdsDTO();
        model.addAttribute("msdsDTO",msdsDTO);
        log.info("msdsDTO=============================" + msdsDTO);
        //ROHS
        partDetailRohsDTO rohsDTO = partDetailService.getRohsData(idx);
        if(rohsDTO == null) rohsDTO = new partDetailRohsDTO();
        model.addAttribute("rohsDTO",rohsDTO);
        log.info("rohsDTO=============================" + rohsDTO);
        //HALOGEN
        partDetailHalGDTO halGDTO = partDetailService.getHalgData(idx);
        if(halGDTO == null) halGDTO = new partDetailHalGDTO();
        model.addAttribute("halGDTO",halGDTO);
        log.info("halGDTO=============================" + halGDTO);
        //ETC
        List<partDetailEtcDTO> etcListDTO =partDetailService.getEtcData(idx);
        if(etcListDTO == null) {
            partDetailEtcDTO etcDTO = new partDetailEtcDTO();
            etcListDTO.add(etcDTO);
        }
        model.addAttribute("etcListDTO",etcListDTO);
        log.info("etcListDTO=============================" + etcListDTO);


        //svhc data 들고가기
        PartDetailSvhcDTO svhcDTO = partDetailService.getDetailSvhcData(idx);
        if(svhcDTO == null) svhcDTO = new PartDetailSvhcDTO();
        model.addAttribute("svhcDTO",svhcDTO);
        log.info("svhcDTO=============================" + svhcDTO);

        //declar

        partDetailDeclarDTO  declarDTO = partDetailService.getDetailDeclarData(idx);
        if(declarDTO == null) declarDTO = new partDetailDeclarDTO();
        model.addAttribute("declarDTO",declarDTO);

        //sccs 성분 rlxk 들고가기
        partDetailSccsDTO sccsDTO = partDetailService.getSccsData(idx);
        if(sccsDTO == null) sccsDTO = new partDetailSccsDTO();
        model.addAttribute("sccsDTO",sccsDTO);
        log.info("sccsDTO=============================" + sccsDTO);

        partDetailIngredDTO ingredDTO = partDetailService.getIngredData(idx);
        if(ingredDTO == null) ingredDTO = new partDetailIngredDTO();
        model.addAttribute("ingredDTO",ingredDTO);
        log.info("ingredDTO=============================" + ingredDTO);

        //보증
        List<partDetailGuarantDTO> guarantListDTO =partDetailService.getGuarantData(idx);
        if(guarantListDTO == null) {
            partDetailGuarantDTO guarantDTO = new partDetailGuarantDTO();
            guarantListDTO.add(guarantDTO);
        }
        model.addAttribute("guarantListDTO",guarantListDTO);
        log.info("guarantListDTO=============================" + guarantListDTO);


        return "approvalState/detail";

    }
}
