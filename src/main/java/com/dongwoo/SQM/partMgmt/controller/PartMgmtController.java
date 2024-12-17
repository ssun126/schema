package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.adminPartMgmt.dto.AdminPartMgmtDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.system.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.Banner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/partMgmt")
public class PartMgmtController {
    private final PartMgmtService partMgmtService;
    private final PartDetailService partDetailService;
    private final MemberService memberService;

    //메인 리스트 화면
    @RequestMapping("/matReg")
    public String initPartMgmt(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        List<HashMap> searchPlantList = partMgmtService.getPlantList();
        model.addAttribute("searchPlantList",searchPlantList);

        try {
            BaseCodeDTO baseCodeDTO = new BaseCodeDTO();
            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            ObjectMapper mapper = new ObjectMapper();

            String sPartCode = GetParam(request, "sPartCode", "");
            String sPartName = GetParam(request, "sPartName", "");
            String sRegUser = GetParam(request, "sRegUser", "");
            String sUseYN = GetParam(request, "sUseYN", "");
            String sPlant = GetParam(request, "sPlant", "");
            String sApproval = GetParam(request, "sApproval", "");
            String sStartDate = GetParam(request, "sStartDate", "");
            String sEndDate = GetParam(request, "sEndDate", "");

            model.addAttribute("sPartCode", sPartCode);
            model.addAttribute("sPartName", sPartName);
            model.addAttribute("sRegUser", sRegUser);
            model.addAttribute("sUseYN", sUseYN);
            model.addAttribute("sPlant", sPlant);
            model.addAttribute("sApproval", sApproval);
            model.addAttribute("sStartDate", sStartDate);
            model.addAttribute("sEndDate", sEndDate);

            PartMgmtDTO parmDTO = new PartMgmtDTO();
            parmDTO.setCOM_CODE(user.getCOM_CODE());
            parmDTO.setPM_PART_CODE(sPartCode);
            parmDTO.setPART_NAME(sPartName);
            parmDTO.setSEARCH_REG_USER(sRegUser);
            parmDTO.setPM_PART_PLANT_CODE(sPlant);
            parmDTO.setPM_ACTIVE_YN(sUseYN);
            parmDTO.setPM_APPROVAL_STATUS(sApproval);
            parmDTO.setSEARCH_PM_SDATE(sStartDate);
            parmDTO.setSEARCH_PM_EDATE(sEndDate);

            List<PartMgmtDTO> partMgmtDTOList = partMgmtService.searchPartMgmt(parmDTO);
            String partMgmtListStr = mapper.writeValueAsString(partMgmtDTOList);

            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(partMgmtListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("partMgmtListStr",partMgmtListStr);
        } catch (Exception e) {
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||" + e.getMessage());
                    printer.close();
                } catch (Exception e2) {
                }

                return "blank";
            } else {
                return  "redirect:/main";
            }
        }

        return "partMgmtList/main";

    }

    @PostMapping("/matRegPopup")
    public String matRegPopup(Model model, HttpServletRequest request, HttpServletResponse response){
        String mode = request.getParameter("mode");
        model.addAttribute("mode",mode);

        String TITLE = "";
        int PM_IDX = 0;
        String PM_PART_CODE = "";
        String PART_NAME = "";
        String PM_PART_PLANT_CODE = "";
        String PM_QUALITY = "";
        String PM_STATUS = "";
        String PM_CHEMICAL_YN = "";
        String COM_NATION = "";

        if (mode.equals("Edit")) {
            TITLE = "수정";
            PM_IDX = Integer.parseInt(GetParam(request,"PM_IDX", "0"));
            PartMgmtDTO partMgmtDTO = partMgmtService.getPartMgmt(PM_IDX);
            if (partMgmtDTO != null) {
                PM_PART_CODE = partMgmtDTO.getPM_PART_CODE();
                PART_NAME = partMgmtDTO.getPART_NAME();
                PM_PART_PLANT_CODE = partMgmtDTO.getPM_PART_PLANT_CODE();
                PM_QUALITY = partMgmtDTO.getPM_QUALITY();
                PM_STATUS = partMgmtDTO.getPM_STATUS();
                PM_CHEMICAL_YN = partMgmtDTO.getPM_CHEMICAL_YN();
                COM_NATION = partMgmtDTO.getCOM_NATION();
            } else {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||자재 정보가 존재하지 않습니다.");
                    printer.close();
                } catch (Exception e2) {
                }

                return "blank";
            }
        } else {
            TITLE = "신규추가";
        }

        model.addAttribute("TITLE",TITLE);
        model.addAttribute("PM_IDX",PM_IDX);
        model.addAttribute("PM_PART_CODE",PM_PART_CODE);
        model.addAttribute("PART_NAME",PART_NAME);
        model.addAttribute("PM_PART_PLANT_CODE",PM_PART_PLANT_CODE);
        model.addAttribute("PM_QUALITY",PM_QUALITY);
        model.addAttribute("PM_STATUS",PM_STATUS);
        model.addAttribute("PM_CHEMICAL_YN",PM_CHEMICAL_YN);
        model.addAttribute("COM_NATION",COM_NATION);

        return "partMgmtList/popup";
    }

    @PostMapping("/searchPartCode")
    public String searchPartCode(Model model, HttpServletRequest request, HttpServletResponse response){
        return "partMgmtList/searchPartCode";
    }

    @PostMapping("/searchPartCodeList")
    public ResponseEntity<?> searchPartCodeList(HttpServletRequest request, HttpServletResponse response) {
        try{
            String code = GetParam(request, "diaSearchPartCode", "");
            String name = GetParam(request, "diaSearchPartName", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<HashMap>  searchPartCodeList = partMgmtService.searchPartCodeList(user.getCOM_CODE(), code, name);

            return ResponseEntity.ok().body(searchPartCodeList);
        }catch(Exception e){
            return ResponseEntity.ok().body("|||[ERROR]|||" + e.getMessage());
        }
    }

    @PostMapping("/setPartMgmtData")
    public ResponseEntity<?> insertData(HttpServletRequest request, HttpServletResponse response) {
        try{
            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String mode = GetParam(request, "mode", "");
            int PM_IDX = Integer.parseInt(GetParam(request,"PM_IDX", "0"));
            String PM_PART_CODE = GetParam(request, "PM_PART_CODE", "");
            String PART_NAME = GetParam(request, "PART_NAME", "");
            String COM_NATION = GetParam(request, "COM_NATION", "");
            String PM_QUALITY = GetParam(request, "PM_QUALITY", "");
            String PM_STATUS = GetParam(request, "PM_STATUS", "");
            String PM_CHEMICAL_YN = GetParam(request, "PM_CHEMICAL_YN", "");
            String PM_PART_PLANT_CODE = GetParam(request, "PM_PART_PLANT_CODE", "");

            PartMgmtDTO partMgmtDTO = new PartMgmtDTO();

            partMgmtDTO.setPM_IDX(PM_IDX);
            partMgmtDTO.setPM_QUALITY(PM_QUALITY);
            partMgmtDTO.setPM_STATUS(PM_STATUS);
            partMgmtDTO.setPM_CHEMICAL_YN(PM_CHEMICAL_YN);

            int resultCnt = 0;
            if(partMgmtDTO.getPM_IDX() == 0){
                partMgmtDTO.setPM_PART_CODE(PM_PART_CODE);
                partMgmtDTO.setPM_PART_PLANT_CODE(PM_PART_PLANT_CODE);
                partMgmtDTO.setPM_APPROVAL_STATUS("0");
                partMgmtDTO.setPM_ACTIVE_YN("INACTIVE");
                partMgmtDTO.setPM_REG_USER(user.getUSER_IDX());

                //insert
                resultCnt = partMgmtService.save(partMgmtDTO);
            }else{
                partMgmtDTO.setPM_MODIFY_USER(user.getUSER_IDX());
                //update
                resultCnt = partMgmtService.updatePartMgmt(partMgmtDTO);
            }

            // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
            if(resultCnt > 0){
                return ResponseEntity.ok("OK");
            }else{
                return ResponseEntity.ok().body("|||[ERROR]|||");
            }
        }catch(Exception e){
            return ResponseEntity.ok().body("|||[ERROR]|||" + e.getMessage());
        }
    }

    @GetMapping("/goReadDetail")
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


        return "partMgmtList/detailread";

    }

    @GetMapping("/updateActive")
    public void updateActive(@RequestParam("PM_ACTIVE_YN") String status,@RequestParam("PM_IDX") int pmidx) {
        partMgmtService.updateActive(status,pmidx);
    }

    //자재코드 리스트
    @GetMapping("/PartCodeList")
    public ResponseEntity<?> partCodeList(){
        try{
            List<HashMap> PartCdList = partMgmtService.getpartCodeList();
            return ResponseEntity.ok(PartCdList);

        }catch(Exception e){
            System.out.println(" 자재코드 리스트 업 에러 : "+e );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }

    }

    ///getPartMgmtData
    @GetMapping("/getPartMgmtData")
    public ResponseEntity<?> getPartMgmtData(@RequestParam("param1") String idx) {
        PartMgmtDTO partMgmtDTO = partMgmtService.getPartMgmtData(idx);

        if (partMgmtDTO != null) {
            return ResponseEntity.ok().body(partMgmtDTO);  // 회사 정보가 있을 경우 frvv5cov응답
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found.");
        }
    }

    @GetMapping("/deletePartMgmt")
    public ResponseEntity<?> deletePartMgmt(@RequestParam("ARR_PM_IDX") String arrIdx){
        int delResult = partMgmtService.deletePartMgmt(arrIdx);
        //int delResult =0;

        if(delResult > 0) {
            return ResponseEntity.ok("Form submitted successfully!");
        }else{
            return ResponseEntity.ok("Form submitted fail!");
        }
    }

    private String GetParam(HttpServletRequest request, String pName, String pDefault) {
        String ParamValue = request.getParameter(pName);

        if (ParamValue == null || ParamValue.isEmpty()) {
            ParamValue = pDefault;
        };

        return ParamValue;
    }
}
