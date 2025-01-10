package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import com.dongwoo.SQM.system.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/partMgmt")
public class PartMgmtController {
    private final PartMgmtService partMgmtService;
    private final MemberService memberService;
    private final com.dongwoo.SQM.siteMgr.service.BaseConfigService BaseConfigService;

    //메인 리스트 화면
    @RequestMapping("/matReg")
    public String initPartMgmt(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        List<HashMap> searchPlantList = partMgmtService.getPlantList();
        model.addAttribute("searchPlantList", searchPlantList);

        try {
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

            model.addAttribute("partMgmtListStr", partMgmtListStr);
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
                return "redirect:/main";
            }
        }

        return "partMgmtList/main";

    }

    @PostMapping("/matRegPopup")
    public String matRegPopup(Model model, HttpServletRequest request, HttpServletResponse response) {
        String mode = request.getParameter("mode");
        model.addAttribute("mode", mode);

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
            PM_IDX = Integer.parseInt(GetParam(request, "PM_IDX", "0"));
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

        model.addAttribute("TITLE", TITLE);
        model.addAttribute("PM_IDX", PM_IDX);
        model.addAttribute("PM_PART_CODE", PM_PART_CODE);
        model.addAttribute("PART_NAME", PART_NAME);
        model.addAttribute("PM_PART_PLANT_CODE", PM_PART_PLANT_CODE);
        model.addAttribute("PM_QUALITY", PM_QUALITY);
        model.addAttribute("PM_STATUS", PM_STATUS);
        model.addAttribute("PM_CHEMICAL_YN", PM_CHEMICAL_YN);
        model.addAttribute("COM_NATION", COM_NATION);

        return "partMgmtList/popup";
    }

    @PostMapping("/searchPartCode")
    public String searchPartCode(Model model, HttpServletRequest request, HttpServletResponse response) {
        return "partMgmtList/searchPartCode";
    }

    @PostMapping("/searchPartCodeList")
    public ResponseEntity<?> searchPartCodeList(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = GetParam(request, "diaSearchPartCode", "");
            String name = GetParam(request, "diaSearchPartName", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<HashMap> searchPartCodeList = partMgmtService.searchPartCodeList(user.getCOM_CODE(), code, name);

            return ResponseEntity.ok().body(searchPartCodeList);
        } catch (Exception e) {
            return ResponseEntity.ok().body("|||[ERROR]|||" + e.getMessage());
        }
    }

    @PostMapping("/setPartMgmtData")
    public ResponseEntity<?> insertData(HttpServletRequest request, HttpServletResponse response) {
        try {
            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String mode = GetParam(request, "mode", "");
            int PM_IDX = Integer.parseInt(GetParam(request, "PM_IDX", "0"));
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
            if (partMgmtDTO.getPM_IDX() == 0) {
                partMgmtDTO.setPM_PART_CODE(PM_PART_CODE);
                partMgmtDTO.setPM_PART_PLANT_CODE(PM_PART_PLANT_CODE);
                partMgmtDTO.setPM_APPROVAL_STATUS("1");
                partMgmtDTO.setPM_ACTIVE_YN("INACTIVE");
                partMgmtDTO.setPM_REG_USER(user.getUSER_IDX());

                //insert
                resultCnt = partMgmtService.save(partMgmtDTO);
            } else {
                partMgmtDTO.setPM_MODIFY_USER(user.getUSER_IDX());
                //update
                resultCnt = partMgmtService.updatePartMgmt(partMgmtDTO);
            }

            // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
            if (resultCnt > 0) {
                return ResponseEntity.ok("OK");
            } else {
                return ResponseEntity.ok().body("|||[ERROR]|||");
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body("|||[ERROR]|||" + e.getMessage());
        }
    }


//
//    @GetMapping("/goReadDetail")
//    public String goReadDetail(@RequestParam("PM_IDX") String idx, Model model, @RequestHeader Map<String, String> header) {
//
//        PartMgmtDTO partMgmtDTO = partMgmtService.getPartData(idx);
//        if (partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
//        model.addAttribute("partMgmtDTO", partMgmtDTO);
//        log.info("partMgmtDTO=============================" + partMgmtDTO);
//
//        //데이터가 있다면 들고와서 뿌려줌
//        //MSDS
//        partDetailMsdsDTO msdsDTO = partMgmtService.getMsdsData(idx);
//        if (msdsDTO == null) msdsDTO = new partDetailMsdsDTO();
//        model.addAttribute("msdsDTO", msdsDTO);
//        log.info("msdsDTO=============================" + msdsDTO);
//        //ROHS
//        partDetailRohsDTO rohsDTO = partMgmtService.getRohsData(idx);
//        if (rohsDTO == null) rohsDTO = new partDetailRohsDTO();
//        model.addAttribute("rohsDTO", rohsDTO);
//        log.info("rohsDTO=============================" + rohsDTO);
//        //HALOGEN
//        partDetailHalGDTO halGDTO = partMgmtService.getHalgData(idx);
//        if (halGDTO == null) halGDTO = new partDetailHalGDTO();
//        model.addAttribute("halGDTO", halGDTO);
//        log.info("halGDTO=============================" + halGDTO);
//        //ETC
//        List<partDetailEtcDTO> etcListDTO = partMgmtService.getEtcData(idx);
//        if (etcListDTO == null) {
//            partDetailEtcDTO etcDTO = new partDetailEtcDTO();
//            etcListDTO.add(etcDTO);
//        }
//        model.addAttribute("etcListDTO", etcListDTO);
//        log.info("etcListDTO=============================" + etcListDTO);
//
//
//        //svhc data 들고가기
//        PartDetailSvhcDTO svhcDTO = partMgmtService.getDetailSvhcData(idx);
//        if (svhcDTO == null) svhcDTO = new PartDetailSvhcDTO();
//        model.addAttribute("svhcDTO", svhcDTO);
//        log.info("svhcDTO=============================" + svhcDTO);
//
//        List<SvhcListDTO> partSvhcDTOList = new ArrayList<>();
//        partSvhcDTOList = partMgmtService.getSvhcData();
//
//        ObjectMapper mapper = new ObjectMapper();
//        String partSvhcListStr = null;
//        try {
//            partSvhcListStr = mapper.writeValueAsString(partSvhcDTOList);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        log.info("partSvhcDTOList=============================" + partSvhcDTOList);
//
//        model.addAttribute("partSvhcList",partSvhcListStr);
//
//
//        //declar
//        partDetailDeclarDTO declarDTO = partMgmtService.getDetailDeclData(idx);
//        if (declarDTO == null) declarDTO = new partDetailDeclarDTO();
//        model.addAttribute("declarDTO", declarDTO);
//
//        List<DeclarationDTO> declDTOList = new ArrayList<>();
//
//        declDTOList = partMgmtService.getDeclData();
//
//        ObjectMapper mapper2 = new ObjectMapper();
//        String partDeclListStr = null;
//        try {
//            partDeclListStr = mapper2.writeValueAsString(declDTOList);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        model.addAttribute("partDeclList",partDeclListStr);
//
//
//        //sccs 성분 rlxk 들고가기
//        partDetailSccsDTO sccsDTO = partMgmtService.getSccsData(idx);
//        if (sccsDTO == null) sccsDTO = new partDetailSccsDTO();
//        model.addAttribute("sccsDTO", sccsDTO);
//        log.info("sccsDTO=============================" + sccsDTO);
//
//        partDetailIngredDTO ingredDTO = partMgmtService.getIngredData(idx);
//        if (ingredDTO == null) ingredDTO = new partDetailIngredDTO();
//        model.addAttribute("ingredDTO", ingredDTO);
//        log.info("ingredDTO=============================" + ingredDTO);
//
//        //보증
//        List<partDetailGuarantDTO> guarantListDTO = partMgmtService.getGuarantData(idx);
//        if (guarantListDTO == null) {
//            partDetailGuarantDTO guarantDTO = new partDetailGuarantDTO();
//            guarantListDTO.add(guarantDTO);
//        }
//        model.addAttribute("guarantListDTO", guarantListDTO);
//        log.info("guarantListDTO=============================" + guarantListDTO);
//
//
//        return "partMgmtList/detailread";
//
//    }

    @GetMapping("/updateActive")
    public void updateActive(@RequestParam("PM_ACTIVE_YN") String status, @RequestParam("PM_IDX") int pmidx) {
        partMgmtService.updateActive(status, pmidx);
    }

    //자재코드 리스트
    @GetMapping("/PartCodeList")
    public ResponseEntity<?> partCodeList() {
        try {
            List<HashMap> PartCdList = partMgmtService.getpartCodeList();
            return ResponseEntity.ok(PartCdList);

        } catch (Exception e) {
            System.out.println(" 자재코드 리스트 업 에러 : " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }

    }

    /// getPartMgmtData
    @GetMapping("/getPartMgmtData")
    public ResponseEntity<?> getPartMgmtData(@RequestParam("param1") String idx) {
        PartMgmtDTO partMgmtDTO = partMgmtService.getPartMgmtData(idx);

        if (partMgmtDTO != null) {
            return ResponseEntity.ok().body(partMgmtDTO);  // 회사 정보가 있을 경우 frvv5cov응답
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found.");
        }
    }

    @PostMapping("/deletePartMgmt")
    //public ResponseEntity<?> deletePartMgmt(@RequestParam("ARR_PM_IDX") String arrIdx){
    public ResponseEntity<?> deletePartMgmt(HttpServletRequest request, HttpSession session) {
        try {
            String arrPmIdx = GetParam(request, "ARR_PM_IDX", "");
            int delResult = partMgmtService.deletePartMgmt(arrPmIdx);
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/updateActiveStatus")
    public ResponseEntity<?> updateActiveStatus(HttpServletRequest request, HttpSession session) {
        try {
            String activePmIdx = GetParam(request, "ACTIVE_IDX", "");
            String inactivePmIdx = GetParam(request, "INACTIVE_IDX", "");
            log.info("test1111-================================="+activePmIdx);

            if( activePmIdx != "") partMgmtService.updateActiveList("ACTIVE", activePmIdx);
            log.info("test1111222222-================================="+inactivePmIdx);
            if( inactivePmIdx != "") partMgmtService.updateActiveList("INACTIVE", inactivePmIdx);

        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }
        return ResponseEntity.ok("OK");
    }


    /*********************************************************************************************************************
     ** Detail v페이지
     ** Msds / Rohs / Halogen 페이지 **
     *********************************************************************************************************************/


    @RequestMapping("/partMgmDetail")
    public String partMgmDetail(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        try {

            String idx = GetParam(request, "PM_IDX", "");
            model.addAttribute("PM_IDX", idx);

            PartMgmtDTO partMgmtDTO = partMgmtService.getPartData(idx);
            if (partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
            model.addAttribute("partMgmtDTO", partMgmtDTO);
            log.info("partMgmtDTO=============================" + partMgmtDTO);

            //데이터가 있다면 들고와서 뿌려줌
            //MSDS
            partDetailMsdsDTO msdsDTO = partMgmtService.getMsdsData(idx);
            if (msdsDTO == null) msdsDTO = new partDetailMsdsDTO();
            model.addAttribute("msdsDTO", msdsDTO);
            log.info("msdsDTO=============================" + msdsDTO);
            //ROHS
            partDetailRohsDTO rohsDTO = partMgmtService.getRohsData(idx);
            if (rohsDTO == null) rohsDTO = new partDetailRohsDTO();
            model.addAttribute("rohsDTO", rohsDTO);
            log.info("rohsDTO=============================" + rohsDTO);
            //HALOGEN
            partDetailHalGDTO halGDTO = partMgmtService.getHalgData(idx);
            if (halGDTO == null) halGDTO = new partDetailHalGDTO();
            model.addAttribute("halGDTO", halGDTO);
            log.info("halGDTO=============================" + halGDTO);
            //ETC
            List<partDetailEtcDTO> etcListDTO =partMgmtService.getEtcData(idx);
            if(etcListDTO == null) {
                partDetailEtcDTO etcDTO = new partDetailEtcDTO();
                etcListDTO.add(etcDTO);
                model.addAttribute("ETC_COUNT",0);
            }else {
                model.addAttribute("ETC_COUNT",etcListDTO.size());
            }
            model.addAttribute("etcListDTO",etcListDTO);
            log.info("etcListDTO=============================" + etcListDTO);


            //
            String Mode = GetParam(request, "MODE", "");
            int status = Integer.parseInt(partMgmtDTO.getPM_APPROVAL_STATUS());
            // 수정버튼 클릭, inactive , STATUS 가 3 이상일 때는 수정페이지
            // STATUS >= 3 검토중일때는 MODE 가 빈값일때는 .전부 READ
            if(status >= 3 && Mode.equals("")){
                //svhc data 들고가기
                PartDetailSvhcDTO svhcDTO = partMgmtService.getDetailSvhcData(idx);
                if (svhcDTO == null) svhcDTO = new PartDetailSvhcDTO();
                model.addAttribute("svhcDTO", svhcDTO);
                log.info("svhcDTO=============================" + svhcDTO);

                List<SvhcListDTO> partSvhcDTOList = new ArrayList<>();
                partSvhcDTOList = partMgmtService.getSvhcData();

                ObjectMapper mapper = new ObjectMapper();
                String partSvhcListStr = null;
                try {
                    partSvhcListStr = mapper.writeValueAsString(partSvhcDTOList);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                log.info("partSvhcDTOList=============================" + partSvhcDTOList);

                model.addAttribute("partSvhcList",partSvhcListStr);


                //declar
                partDetailDeclarDTO declarDTO = partMgmtService.getDetailDeclData(idx);
                if (declarDTO == null) declarDTO = new partDetailDeclarDTO();
                model.addAttribute("declarDTO", declarDTO);

                List<DeclarationDTO> declDTOList = new ArrayList<>();

                declDTOList = partMgmtService.getDeclData();

                ObjectMapper mapper2 = new ObjectMapper();
                String partDeclListStr = null;
                try {
                    partDeclListStr = mapper2.writeValueAsString(declDTOList);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                model.addAttribute("partDeclList",partDeclListStr);


                //sccs 성분 rlxk 들고가기
                partDetailSccsDTO sccsDTO = partMgmtService.getSccsData(idx);
                if (sccsDTO == null) sccsDTO = new partDetailSccsDTO();
                model.addAttribute("sccsDTO", sccsDTO);
                log.info("sccsDTO=============================" + sccsDTO);

                partDetailIngredDTO ingredDTO = partMgmtService.getIngredData(idx);
                if (ingredDTO == null) ingredDTO = new partDetailIngredDTO();
                model.addAttribute("ingredDTO", ingredDTO);
                log.info("ingredDTO=============================" + ingredDTO);

                //보증
                List<partDetailGuarantDTO> guarantListDTO = partMgmtService.getGuarantData(idx);
                if (guarantListDTO == null) {
                    partDetailGuarantDTO guarantDTO = new partDetailGuarantDTO();
                    guarantListDTO.add(guarantDTO);
                }
                model.addAttribute("guarantListDTO", guarantListDTO);
                log.info("guarantListDTO=============================" + guarantListDTO);

                return "partMgmtList/detailread";
            }

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
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||" + e.getMessage());
                    printer.close();
                } catch (Exception e2) {
                }

                //return  "redirect:/main";
                return "blank";
            }
        }

        return "partMgmtList/partMgmDetail";
        //return "partMgmtList/detail";


    }

    @RequestMapping("/goMsdsSave")
    //public String partMgmtGoSvhcDetail(Model model, HttpServletRequest request
    public ResponseEntity<?> partMgmtGoSvhcDetail(Model model, HttpServletRequest request
            , HttpServletResponse response
            , @RequestParam(value = "MSDS_FILE", required = false) MultipartFile msdsFile
            , @RequestParam(value = "ROHS_FILE", required = false) MultipartFile rohsFile
            , @RequestParam(value = "HALOGEN_FILE", required = false) MultipartFile halgFile
            , @RequestParam(value = "ETC_FILE", required = false) MultipartFile[] etcFile
            , @ModelAttribute partDetailMsdsDTO msdsDTO
            , @ModelAttribute partDetailRohsDTO rohsDTO
            , @ModelAttribute partDetailHalGDTO halGDTO) {

        int result = 0;
        String PM_PART_CODE = GetParam(request, "PM_PART_CODE", "");
        int PM_IDX = Integer.parseInt(GetParam(request, "PM_IDX", "0"));
        String flag = GetParam(request, "SaveMode","");
        log.info("flag================"+flag);

        try {

            //msds
//        String saveMode = GetParam(request,"SaveMode","");
//
            String MSDS_IDX = GetParam(request, "MSDS_IDX", "");

            //msds file
//            if (msdsDTO.getMSDS_FILE_STATUS().equals("DEL")) {
//                msdsDTO.setMSDS_FILE_NAME("");
//                msdsDTO.setMSDS_FILE_PATH("");
//            }
            partDetailMsdsDTO OrigMsdsDTO = partMgmtService.getOrignMsdsData(PM_IDX);

            if(msdsFile != null) {
                if (!msdsFile.isEmpty()) {
                    String msds_filepath = partMgmtService.uploadFileData(PM_PART_CODE, msdsFile);
                    String msds_filename = msdsFile.getOriginalFilename();

//            MSDS_FILE_NAME=etc_filename;
//            MSDS_FILE_PATH = etc_filepath;
                    msdsDTO.setMSDS_FILE_NAME(msds_filename);
                    msdsDTO.setMSDS_FILE_PATH(msds_filepath);

                }
            }else{
                msdsDTO.setMSDS_FILE_NAME(OrigMsdsDTO.getMSDS_FILE_NAME());
                msdsDTO.setMSDS_FILE_PATH(OrigMsdsDTO.getMSDS_FILE_PATH());
            }

            //승인이 한번 떨어진 이후, 작성중 상태이거나 승인 상태에서는 재보증 확인
            String appData =  GetParam(request, "PM_APPROVAL_DATE", "");
            String appStatus = GetParam(request, "PM_APPROVAL_STATUS", "");
            if( !appData.equals("") && (appStatus.equals("2") || appStatus.equals("4"))) {

                String MsdsCheck = "";

                partDetailRohsDTO OrigRohsDTO = partMgmtService.getOrignRohsData(PM_IDX);
                partDetailHalGDTO OrigHalgDTO = partMgmtService.getOrignHalgData(PM_IDX);

                boolean changeData = false; //바뀐 데이터가 없으면 저장 안됨
                String chkData = "";
                chkData=compareObjects(msdsDTO,OrigMsdsDTO);
                if(!chkData.equals("")) {
                    String errMessage ="";
                    String[] spData = chkData.split("\\|");
                    for(int i = 0; i<spData.length; i++){
                        String FieldName = spData[i].split(":")[0];
                        //날짜 데이터 바뀌었을 때
                        if(FieldName.endsWith("_DATE")){
                            if( msdsDTO.getMSDS_CONFIRM_CHK() == null) {
                                errMessage = "|||[ERROR]|||MSDS 전자보증서 제출 확인 체크를 해주세요";
                            }
                            //return ResponseEntity.ok("NEXT|||"+PM_IDX);
                        }else{
                            // 여기 들어오는 것 자체가 바뀐 내용이 있는 것.
                            changeData = true;
                        }
                    }
                    // 날짜만! 바뀔때만 보증 체크해줘야함
                    if( !changeData && !errMessage.equals("")){
                        return ResponseEntity.ok("|||[ERROR]|||"+errMessage);
                    }
                    // 변경된 데이터가 있고
                    if( (changeData && msdsDTO.getMSDS_CONFIRM_CHK() != null) ){
                        return ResponseEntity.ok("|||[ERROR]||| 갱신된 데이터가 존재합니다. MSDS 전자보증서 제출 확인 체크 해제해 주세요");
                    }
//                    if( errMessage.equals("") && msdsDTO.getMSDS_CONFIRM_CHK() != null ) {
//                        return ResponseEntity.ok("|||[ERROR]||| MSDS 작성일을 변경하거나 MSDS 전자보증서 제출 확인 체크 해제해 주세요");
//                    }



                }else{
                    //바뀐데이터가 없다
                    return ResponseEntity.ok("|||[ERROR]||| MSDS 변경된 데이터가 없습니다");
                }

            }

            log.info("MSDS_IDX=================="+MSDS_IDX);
            if (MSDS_IDX == "") {
                //신규
                result += partMgmtService.insertMsdsData(msdsDTO);
            } else {
                //기존
                result += partMgmtService.updateMsdsData(msdsDTO);
            }


            //Rohs
            String ROHS_IDX = GetParam(request, "ROHS_IDX", "");


            //ROHS file
            //if (!rohsFile.isEmpty()) {
            if (rohsFile != null) {
                String etc_filepath = partMgmtService.uploadFileData(PM_PART_CODE, rohsFile);
                String etc_filename = rohsFile.getOriginalFilename();

//            MSDS_FILE_NAME=etc_filename;
//            MSDS_FILE_PATH = etc_filepath;
                rohsDTO.setROHS_FILE_NAME(etc_filename);
                rohsDTO.setROHS_FILE_PATH(etc_filepath);

            }


            if (ROHS_IDX == "") {
                //신규
                result += partMgmtService.insertRohsData(rohsDTO);
            } else {
                //기존
                result += partMgmtService.updateRohsData(rohsDTO);
            }


            //halogen
            String HALOGEN_IDX = GetParam(request, "HALOGEN_IDX", "");


            //HALOGEN FILE
            if (halgFile != null) {
                String etc_filepath = partMgmtService.uploadFileData(PM_PART_CODE, halgFile);
                String etc_filename = halgFile.getOriginalFilename();

                halGDTO.setHALOGEN_FILE_NAME(etc_filename);
                halGDTO.setHALOGEN_FILE_PATH(etc_filepath);

            }


            if (ROHS_IDX == "") {
                //신규
                result += partMgmtService.insertHalogenData(halGDTO);
            } else {
                //기존
                result += partMgmtService.updateHalogenData(halGDTO);
            }

            //etc
            int EtcCount = Integer.parseInt(GetParam(request, "etcCount", "0"));
            String ETC_IDX = GetParam(request, "ETC_IDX", "");
            log.info("test00000============"+result+"==========PM_IDX : "+PM_IDX);

            //삭제된 etc 데이터 db에서 삭제
            String ETC_DEL_IDX = GetParam(request,"ETC_DEL_IDX","");
            String[] ArrDelIdx = ETC_DEL_IDX.split(",");
            for(int j =0; j< ArrDelIdx.length; j++) {
                if(!ArrDelIdx[j].equals("") ){
                    partMgmtService.deleteEtcData(Integer.parseInt(ArrDelIdx[j]));
                }
            }


            //List<partDetailEtcDTO> etcDTOList = new ArrayList<>();
            for (int i = 0; i < EtcCount; i++) {
                if(request.getParameter("ETC_IDX"+(i+1)) != null) {
                    partDetailEtcDTO etcDTO = new partDetailEtcDTO();
                    etcDTO.setETC_IDX(GetParam(request, "ETC_IDX" + (i+1), ""));
                    etcDTO.setPM_IDX(GetParam(request, "PM_IDX" , ""));
                    etcDTO.setETC_CONFIRM_DATE(GetParam(request, "ETC_CONFIRM_DATE" + (i+1), ""));
                    etcDTO.setETC_ANALYSE_ENTRY(GetParam(request, "ETC_ANALYSE_ENTRY" + (i+1), ""));
                    etcDTO.setETC_ANALYSE_RESULT(GetParam(request, "ETC_ANALYSE_RESULT" + (i+1), ""));

                    MultipartFile files = ((StandardMultipartHttpServletRequest) request).getFile("ETC_FILE" + (i+1));

                    if(files != null){

                            String etc_filepath = partMgmtService.uploadFileData(PM_PART_CODE, files);
                            String etc_filename = files.getOriginalFilename();

                            etcDTO.setETC_FILE_NAME(etc_filename);
                            etcDTO.setETC_FILE_PATH(etc_filepath);

                    }

                    if(GetParam(request, "ETC_IDX" + (i+1), "").equals("")) {
                        //신규
                        result += partMgmtService.insertEtcData(etcDTO);
                    }else {
                        result += partMgmtService.updateEtcData(etcDTO);
                    }

                }

            }
            log.info("test1111111============"+result+"==========PM_IDX : "+PM_IDX);
            if (result > 0) {
                partMgmtService.updateApprovalStatus(PM_IDX, "2");
            }

        } catch (Exception e) {
            log.info("ERROR====================="+ e.getMessage());

            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        log.info("flag_Lase================"+flag);

        if(flag.equals("Save")) {
            return ResponseEntity.ok("OK");
        }else{
            return ResponseEntity.ok("NEXT|||"+PM_IDX);
        }

    }

    @RequestMapping("/DeleteFileDetail")
    public ResponseEntity<?> DeleteFileDetail(Model model, HttpServletRequest request){

        String PM_IDX = GetParam(request,"PM_IDX","");
        String Mode = GetParam(request,"MODE","");
        String strIdx = GetParam(request,"IDX","0");

        String path = "";
        String name = "";

        int idx = Integer.parseInt(strIdx);

        if(Mode.equals("MSDS")){
            partDetailMsdsDTO msdsDTO = partMgmtService.getMsdsData(PM_IDX);

            path = msdsDTO.getMSDS_FILE_PATH();
            name= msdsDTO.getMSDS_FILE_NAME();

            if(partMgmtService.deleteFileData(name,path)){
                partMgmtService.MsdsDeleteFile(idx);
            }
        }else if(Mode.equals("ROHS")){
            partDetailRohsDTO rohsDTO = partMgmtService.getRohsData(PM_IDX);

            path = rohsDTO.getROHS_FILE_PATH();
            name= rohsDTO.getROHS_FILE_NAME();

            if(partMgmtService.deleteFileData(name,path)){
                partMgmtService.RohsDeleteFile(idx);
            }
        }else if(Mode.equals("HALOGEN")){
            partDetailHalGDTO halgDTO = partMgmtService.getHalgData(PM_IDX);

            path = halgDTO.getHALOGEN_FILE_PATH();
            name= halgDTO.getHALOGEN_FILE_NAME();

            if(partMgmtService.deleteFileData(name,path)){
                partMgmtService.HalogenDeleteFile(idx);
            }
        }else if(Mode.equals("ETC")){
            partDetailEtcDTO etcDTO = partMgmtService.getEtcDataIdx(idx);

            path = etcDTO.getETC_FILE_PATH();
            name= etcDTO.getETC_FILE_NAME();

            if(partMgmtService.deleteFileData(name,path)){
                partMgmtService.EtcDeleteFile(idx);
            }
        }else if(Mode.equals("SVHC")){
            PartDetailSvhcDTO svhcDTO = partMgmtService.getDetailSvhcData(PM_IDX);

            path = svhcDTO.getFILE_PATH();
            name= svhcDTO.getFILE_NAME();

            if(partMgmtService.deleteFileData(name,path)){
                partMgmtService.SvhcDeleteFile(idx);
            }
        }else if(Mode.equals("DECL")){
            partDetailDeclarDTO declarDTO = partMgmtService.getDetailDeclData(PM_IDX);

            path = declarDTO.getFILE_PATH();
            name= declarDTO.getFILE_NAME();

            if(partMgmtService.deleteFileData(name,path)){
                partMgmtService.DeclDeleteFile(idx);
            }
        }else if(Mode.equals("SCCS")){
            partDetailSccsDTO sccsDTO = partMgmtService.getSccsData(PM_IDX);

            path = sccsDTO.getSCCS_FILE_PATH();
            name= sccsDTO.getSCCS_FILE_NAME();

            if(partMgmtService.deleteFileData(name,path)){
                partMgmtService.SccsDeleteFile(idx);
            }
        }else if(Mode.equals("INGRED")){
            partDetailIngredDTO ingredDTO = partMgmtService.getIngredData(PM_IDX);

            path = ingredDTO.getINGRED_FILE_PATH();
            name= ingredDTO.getINGRED_FILE_NAME();

            if(partMgmtService.deleteFileData(name,path)){
                partMgmtService.IngredDeleteFile(idx);
            }
        }else if(Mode.equals("GUARANT")){
            partDetailGuarantDTO guarantDTO = partMgmtService.getGuarantDataIdx(idx);

            path = guarantDTO.getGUARANT_FILE_PATH();
            name= guarantDTO.getGUARANT_FILE_NAME();

            if(partMgmtService.deleteFileData(name,path)){
                partMgmtService.GuarantDeleteFile(idx);
            }
        }

//        if(flag.equals("Save")) {
//            return ResponseEntity.ok("OK");
//        }else{
//            return ResponseEntity.ok("NEXT|||"+PM_IDX);
//        }
        return ResponseEntity.ok("OK");
    }



    /*********************************************************************************************************************
     ** Detail v페이지
     ** SVHC 페이지 **
     *********************************************************************************************************************/

    @RequestMapping("/goSvhcDetail")
    public String svhcDetail(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        try {
            String idx = GetParam(request, "PM_IDX", "");
            model.addAttribute("PM_IDX", idx);

            PartMgmtDTO partMgmtDTO = partMgmtService.getPartData(idx);
            if (partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
            model.addAttribute("partMgmtDTO", partMgmtDTO);
            log.info("partMgmtDTO=============================" + partMgmtDTO);

            //svhc data 들고가기
            PartDetailSvhcDTO svhcDTO = partMgmtService.getDetailSvhcData(idx);
            List<SvhcListDTO> partSvhcDTOList = new ArrayList<>();
            if(svhcDTO == null) {
                svhcDTO = new PartDetailSvhcDTO();

                BaseConfigDTO baseConfigDTOInfo = BaseConfigService.getBaseConfig_InfoCode("SVHC_ROW_COUNT");
                svhcDTO.setWARRANTY_ITEM(baseConfigDTOInfo.getCONFIG_VALUE());
            }

            int yCnt = 0;
            if(svhcDTO.getAPPLICABLE_NO() != null) {
                if(svhcDTO.getAPPLICABLE_NO().trim() != "" ) {
                    yCnt= svhcDTO.getAPPLICABLE_NO().split(",").length;
                }
            }
            model.addAttribute("SVHC_YCNT", yCnt);



//            if(svhcDTO.getDATA_GUBUN().equals("WRITE")){
//                partSvhcDTOList = partMgmtService.getSvhcData();
//            }
            partSvhcDTOList = partMgmtService.getSvhcData();

            ObjectMapper mapper = new ObjectMapper();
            String partSvhcListStr = mapper.writeValueAsString(partSvhcDTOList);

            model.addAttribute("svhcDTO",svhcDTO);
            log.info("svhcDTO=============================" + svhcDTO);

            if(header.get("requesttype") != null && header.get("requesttype").equals("ajax")){
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(partSvhcListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("partSvhcList",partSvhcListStr);

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
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||" + e.getMessage());
                    printer.close();
                } catch (Exception e2) {
                }

                //return  "redirect:/main";
                return "blank";
            }
        }

        return "partMgmtList/partMgmDetailSvhc";


    }

    @RequestMapping("/setSvhcExcelData")
    public String setSvhcExcelData(HttpServletRequest request, HttpServletResponse response){
        String partSvhcListStr = null;
        try{
            MultipartFile files = ((StandardMultipartHttpServletRequest) request).getFile("SVHC_FILE");

            //xlsx 인지 xls 인지 구분
            String fileName = files.getOriginalFilename();
            List<SvhcListDTO> svhcListDTOList = new ArrayList<>();

            if(fileName.endsWith(".xlsx")) {
                XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
                XSSFSheet worksheet = workbook.getSheetAt(0);


                for(int i = 19; i<worksheet.getPhysicalNumberOfRows()-1; i++){
                    SvhcListDTO svhcListDTO = new SvhcListDTO();

                    DataFormatter formatter = new DataFormatter();
                    XSSFRow row = worksheet.getRow(i);

                    String SVHC_NUM = formatter.formatCellValue((row.getCell(0)));
                    String SVHC_NAME = formatter.formatCellValue((row.getCell(1)));

                    String SVHC_CASNUM = formatter.formatCellValue((row.getCell(10)));
                    String SVHC_EUNUM = formatter.formatCellValue((row.getCell(11)));
                    String SVHC_YN = formatter.formatCellValue((row.getCell(12)));

                    svhcListDTO.setSVHC_NUM(SVHC_NUM);
                    svhcListDTO.setSVHC_NAME(SVHC_NAME.replaceAll("'","''"));
                    svhcListDTO.setSVHC_CASNUM(SVHC_CASNUM);
                    svhcListDTO.setSVHC_EUNUM(SVHC_EUNUM);
                    svhcListDTO.setSVHC_YN(SVHC_YN);

                    svhcListDTOList.add(svhcListDTO);
                }
            }else if(fileName.endsWith(".xls")){
                HSSFWorkbook workbook = new HSSFWorkbook(files.getInputStream());
                HSSFSheet worksheet = workbook.getSheetAt(0);


                for(int i = 19; i<worksheet.getPhysicalNumberOfRows()-1; i++){
                    SvhcListDTO svhcListDTO = new SvhcListDTO();

                    DataFormatter formatter = new DataFormatter();
                    HSSFRow row = worksheet.getRow(i);

                    String SVHC_NUM = formatter.formatCellValue((row.getCell(0)));
                    String SVHC_NAME = formatter.formatCellValue((row.getCell(1)));

                    String SVHC_CASNUM = formatter.formatCellValue((row.getCell(10)));
                    String SVHC_EUNUM = formatter.formatCellValue((row.getCell(11)));
                    String SVHC_YN = formatter.formatCellValue((row.getCell(12)));

                    svhcListDTO.setSVHC_NUM(SVHC_NUM);
                    svhcListDTO.setSVHC_NAME(SVHC_NAME.replaceAll("'","''"));
                    svhcListDTO.setSVHC_CASNUM(SVHC_CASNUM);
                    svhcListDTO.setSVHC_EUNUM(SVHC_EUNUM);
                    svhcListDTO.setSVHC_YN(SVHC_YN);

                    svhcListDTOList.add(svhcListDTO);
                }
            }else{
                partSvhcListStr="|||[ERROR]||| Invalid file format. Only .xls and .xlsx are supported.";
                PrintWriter printer = response.getWriter();
                printer.print(partSvhcListStr);
                printer.close();

                return "blank";
            }


            ObjectMapper mapper = new ObjectMapper();
            partSvhcListStr = mapper.writeValueAsString(svhcListDTOList);

            PrintWriter printer = response.getWriter();
            printer.print(partSvhcListStr);
            printer.close();


            } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "blank";

    }

    @RequestMapping("/getSvhcListData")
    public String  getSvhcData(  HttpServletResponse response,  @RequestHeader Map<String, String> header ){
        String partSvhcListStr = null;
        try {
            log.info("test=-11111=================");
            ObjectMapper mapper = new ObjectMapper();
            List<SvhcListDTO> svhcList = partMgmtService.getSvhcData();

            partSvhcListStr = mapper.writeValueAsString(svhcList);

            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(partSvhcListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //log.info("test=-11111================="+svhcList);

        return partSvhcListStr;
    }


    @RequestMapping("/goSvhcSave")
    //public String partMgmtGoSvhcDetail(Model model, HttpServletRequest request
    public ResponseEntity<?> partMgmtSvhcSave(Model model, HttpServletRequest request
                                                , HttpServletResponse response
                                                , @RequestParam(value = "SVHC_FILE", required = false) MultipartFile svhcFile
                                                , @ModelAttribute PartDetailSvhcDTO svhcDTO) {

        int result = 0;
        String PM_PART_CODE = GetParam(request, "PM_PART_CODE", "");
        int PM_IDX = Integer.parseInt(GetParam(request, "PM_IDX", "0"));
        String flag = GetParam(request, "SaveMode","");
        log.info("flag================"+flag);

        String blobData = GetParam(request, "blobData", "");
        String Warant = GetParam(request, "WARRANTY_ITEM", "");
        String date = GetParam(request, "CONFIRM_DATE", "").replaceAll("-","");
        String gubun = GetParam(request, "DATA_GUBN", "").replaceAll("-","");
        if(gubun.equals("WRITE")){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                //Map<String, Object> dataMap = objectMapper.readValue(blobData, Map.class);
                List<Map<String, Object>> dataList = objectMapper.readValue(blobData, new TypeReference<>() {});
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("SVHC Data");

                // 헤더 작성
                Row headerRow = sheet.createRow(0);
                if (!dataList.isEmpty()) {
                    int cellIdx = 0;
                    for (String key : dataList.get(0).keySet()) {
                        Cell cell = headerRow.createCell(cellIdx++);
                        cell.setCellValue(key);
                    }
                }

                // 데이터 작성
                int rowIdx = 1;
                for (Map<String, Object> data : dataList) {
                    Row row = sheet.createRow(rowIdx++);
                    int cellIdx = 0;
                    for (Object value : data.values()) {
                        Cell cell = row.createCell(cellIdx++);
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }

                // Excel 파일 저장
                try (FileOutputStream fileOut = new FileOutputStream("C:/output/send/svhc/SVHC_"+Warant+"_"+date+".xlsx")) {
                    workbook.write(fileOut);

                    svhcDTO.setSEND_FILE_PATH("C:/output/uploads/send/svhc/");
                    svhcDTO.setSEND_FILE_NAME("svhc_"+Warant+"_"+date+".xlsx");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Excel 파일이 생성되었습니다.");

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }else{
            try {
                if (svhcFile != null && !svhcFile.isEmpty()) {
                    // 1. 파일 저장 경로와 파일명 설정
                    String uploadDir = "C:/output/send/svhc_files"; // 저장할 디렉터리
//                    String fileName = "custom_name_" + System.currentTimeMillis() + "."
//                            + getFileExtension(declFile.getOriginalFilename()); // 새로운 파일명 생성
                    String fileName = "SVHC_"+Warant+"_"+date+".xlsx";

                    File directory = new File(uploadDir);
                    if (!directory.exists()) {
                        directory.mkdirs(); // 디렉터리가 없으면 생성
                    }

                    // 2. 파일 저장
                    File destinationFile = new File(directory, fileName);
                    svhcFile.transferTo(destinationFile);

                    // 3. 저장된 파일 정보 처리
                    System.out.println("파일이 저장되었습니다: " + destinationFile.getAbsolutePath());

                    // 필요하면 declDTO에 파일 경로나 이름 저장
                    svhcDTO.setSEND_FILE_PATH(destinationFile.getAbsolutePath());
                    svhcDTO.setSEND_FILE_NAME(fileName);

                    //return ResponseEntity.ok("파일 저장 성공: " + fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("파일 저장 중 오류 발생: " + e.getMessage());
            }
        }


        try {

            //msds
            String SVHC_IDX = GetParam(request, "SVHC_IDX", "");
            String FILE_STATUS = GetParam(request,"FILE_STATUS","");
            String DATA_GUBUN = GetParam(request,"DATA_GUBUN","");

            //msds file
//            if (FILE_STATUS.equals("DEL") || DATA_GUBUN.equals("WRITE")) {
//                svhcDTO.setFILE_NAME("");
//                svhcDTO.setFILE_PATH("");
//            }

            //msds file
            if(svhcFile != null){
                if (!svhcFile.isEmpty()) {
                    String etc_filepath = partMgmtService.uploadFileData(PM_PART_CODE, svhcFile);
                    String etc_filename = svhcFile.getOriginalFilename();

                    svhcDTO.setFILE_NAME(etc_filename);
                    svhcDTO.setFILE_PATH(etc_filepath);

                }
            }

            log.info("SVHC_IDX=================="+SVHC_IDX);
            if (SVHC_IDX == "") {
                //신규
                result += partMgmtService.insertSvhcData(svhcDTO);
            } else {
                //기존
                result += partMgmtService.updateSvhcData(svhcDTO);
            }

        } catch (Exception e) {
            log.info("ERROR====================="+ e.getMessage());

            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        log.info("flag_Lase================"+flag);

        if(flag.equals("Save")) {
            return ResponseEntity.ok("OK");
        }else{
            return ResponseEntity.ok("NEXT|||"+PM_IDX);
        }

    }


    /*********************************************************************************************************************
     ** Detail v페이지
     ** Declaration Letter 페이지 **
     *********************************************************************************************************************/

    @RequestMapping("/goDeclDetail")
    public String DeclDetail(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        try {
            String idx = GetParam(request, "PM_IDX", "");
            model.addAttribute("PM_IDX", idx);

            PartMgmtDTO partMgmtDTO = partMgmtService.getPartData(idx);
            if (partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
            model.addAttribute("partMgmtDTO", partMgmtDTO);
            log.info("partMgmtDTO=============================" + partMgmtDTO);

            //svhc data 들고가기
            partDetailDeclarDTO declDTO = partMgmtService.getDetailDeclData(idx);
            List<DeclarationDTO> declDTOList = new ArrayList<>();
            if(declDTO == null) {
                declDTO = new partDetailDeclarDTO();

                BaseConfigDTO baseConfigDTOInfo = BaseConfigService.getBaseConfig_InfoCode("DECLARATIONREV");
                declDTO.setWARRANTY_ITEM(baseConfigDTOInfo.getCONFIG_VALUE());
            }
            model.addAttribute("declDTO",declDTO);
            log.info("svhcDTO=============================" + declDTO);

            declDTOList = partMgmtService.getDeclData();

            ObjectMapper mapper = new ObjectMapper();
            String partDeclListStr = mapper.writeValueAsString(declDTOList);

            if(header.get("requesttype") != null && header.get("requesttype").equals("ajax")){
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(partDeclListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("partDeclList",partDeclListStr);

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
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||" + e.getMessage());
                    printer.close();
                } catch (Exception e2) {
                }

                //return  "redirect:/main";
                return "blank";
            }
        }

        //return "partMgmtList/svhcDetail";
        return "partMgmtList/partMgmDetailDecl";


    }

    @RequestMapping("/setDeclExcelData")
    public String setDeclExcelData(HttpServletRequest request, HttpServletResponse response){
        String partDeclListStr = null;
        try{
            MultipartFile files = ((StandardMultipartHttpServletRequest) request).getFile("DECL_FILE");

            //xlsx 인지 xls 인지 구분
            String fileName = files.getOriginalFilename();
            List<DeclarationDTO> declarationDTOList = new ArrayList<>();

            PrintWriter printer = response.getWriter();

            if(fileName.endsWith(".xlsx")) {
                XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
                XSSFSheet worksheet = workbook.getSheetAt(0);


                for(int i = 3; i<worksheet.getPhysicalNumberOfRows(); i++){
                    DeclarationDTO declarationDTO = new DeclarationDTO();

                    DataFormatter formatter = new DataFormatter();
                    XSSFRow row = worksheet.getRow(i);

                    String DECL_NUM = formatter.formatCellValue(row.getCell(1));
                    String DECL_SUB_NUM = formatter.formatCellValue(row.getCell(2));
                    String DECL_NAME = formatter.formatCellValue(row.getCell(3));
                    String DECL_CASNUM = formatter.formatCellValue(row.getCell(4));
                    String DECL_WEIGHT = formatter.formatCellValue(row.getCell(5));
                    String DECL_CLASS = formatter.formatCellValue(row.getCell(6));
                    String DECL_GROUND = formatter.formatCellValue(row.getCell(7));
                    String DECL_YN = formatter.formatCellValue(row.getCell(8));

                    log.info("엑셀 값 : " +DECL_NUM+ " !!!"+DECL_SUB_NUM+ " !!!"+DECL_NAME+ " !!!"+DECL_CASNUM+ " !!!"+DECL_WEIGHT+ " !!!"+DECL_CLASS+ " !!!"+DECL_GROUND);
                    declarationDTO.setDECL_NUM(DECL_NUM);
                    declarationDTO.setDECL_SUB_NUM(DECL_SUB_NUM);
                    declarationDTO.setDECL_NAME(DECL_NAME);
                    declarationDTO.setDECL_CASNUM(DECL_CASNUM);
                    declarationDTO.setDECL_WEIGHT(DECL_WEIGHT);
                    declarationDTO.setDECL_CLASS(DECL_CLASS);
                    declarationDTO.setDECL_GROUND(DECL_GROUND);
                    declarationDTO.setDECL_YN(DECL_YN);

                    declarationDTOList.add(declarationDTO);

                }
            }else if(fileName.endsWith(".xls")){
                HSSFWorkbook workbook = new HSSFWorkbook(files.getInputStream());
                HSSFSheet worksheet = workbook.getSheetAt(0);


                for(int i = 3; i<worksheet.getPhysicalNumberOfRows(); i++){
                    DeclarationDTO declarationDTO = new DeclarationDTO();

                    DataFormatter formatter = new DataFormatter();
                    HSSFRow row = worksheet.getRow(i);

                    String DECL_NUM = formatter.formatCellValue(row.getCell(1));
                    String DECL_SUB_NUM = formatter.formatCellValue(row.getCell(2));
                    String DECL_NAME = formatter.formatCellValue(row.getCell(3));
                    String DECL_CASNUM = formatter.formatCellValue(row.getCell(4));
                    String DECL_WEIGHT = formatter.formatCellValue(row.getCell(5));
                    String DECL_CLASS = formatter.formatCellValue(row.getCell(6));
                    String DECL_GROUND = formatter.formatCellValue(row.getCell(7));
                    String DECL_YN = formatter.formatCellValue(row.getCell(8));

                    log.info("엑셀 값 : " +DECL_NUM+ " !!!"+DECL_SUB_NUM+ " !!!"+DECL_NAME+ " !!!"+DECL_CASNUM+ " !!!"+DECL_WEIGHT+ " !!!"+DECL_CLASS+ " !!!"+DECL_GROUND);
                    declarationDTO.setDECL_NUM(DECL_NUM);
                    declarationDTO.setDECL_SUB_NUM(DECL_SUB_NUM);
                    declarationDTO.setDECL_NAME(DECL_NAME);
                    declarationDTO.setDECL_CASNUM(DECL_CASNUM);
                    declarationDTO.setDECL_WEIGHT(DECL_WEIGHT);
                    declarationDTO.setDECL_CLASS(DECL_CLASS);
                    declarationDTO.setDECL_GROUND(DECL_GROUND);
                    declarationDTO.setDECL_YN(DECL_YN);

                    declarationDTOList.add(declarationDTO);

                }
            }else{
                partDeclListStr="|||[ERROR]||| Invalid file format. Only .xls and .xlsx are supported.";
                printer.print(partDeclListStr);
                printer.close();

                return "blank";
            }


            ObjectMapper mapper = new ObjectMapper();
            partDeclListStr = mapper.writeValueAsString(declarationDTOList);


            printer.print(partDeclListStr);
            printer.close();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "blank";

    }

    @RequestMapping("/getDeclListData")
    public String  getDeclData(  HttpServletResponse response,  @RequestHeader Map<String, String> header ){
        String partDeclListStr = null;
        try {
            log.info("test=-11111=================");
            ObjectMapper mapper = new ObjectMapper();
            List<DeclarationDTO> declList = partMgmtService.getDeclData();

            partDeclListStr = mapper.writeValueAsString(declList);


            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(partDeclListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //log.info("test=-11111================="+svhcList);

        return partDeclListStr;
    }

    @RequestMapping("/goDeclSave")
    //public String partMgmtGoSvhcDetail(Model model, HttpServletRequest request
    public ResponseEntity<?> partMgmtDeclSave(Model model, HttpServletRequest request
                                                , HttpServletResponse response
                                                , @RequestParam(value = "DECL_FILE", required = false) MultipartFile declFile
                                                , @ModelAttribute partDetailDeclarDTO declDTO) {

        int result = 0;
        String PM_PART_CODE = GetParam(request, "PM_PART_CODE", "");
        int PM_IDX = Integer.parseInt(GetParam(request, "PM_IDX", "0"));
        String flag = GetParam(request, "SaveMode","");
        log.info("flag================"+flag);

        String blobData = GetParam(request, "blobData", "");
        String Warant = GetParam(request, "WARRANTY_ITEM", "");
        String date = GetParam(request, "CONFIRM_DATE", "").replaceAll("-","");
        String gubun = GetParam(request, "DATA_GUBN", "").replaceAll("-","");
        if(gubun.equals("WRITE")){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                //Map<String, Object> dataMap = objectMapper.readValue(blobData, Map.class);
                List<Map<String, Object>> dataList = objectMapper.readValue(blobData, new TypeReference<>() {});
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("DECL Data");

                // 헤더 작성
                Row headerRow = sheet.createRow(0);
                if (!dataList.isEmpty()) {
                    int cellIdx = 0;
                    for (String key : dataList.get(0).keySet()) {
                        Cell cell = headerRow.createCell(cellIdx++);
                        cell.setCellValue(key);
                    }
                }

                // 데이터 작성
                int rowIdx = 1;
                for (Map<String, Object> data : dataList) {
                    Row row = sheet.createRow(rowIdx++);
                    int cellIdx = 0;
                    for (Object value : data.values()) {
                        Cell cell = row.createCell(cellIdx++);
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }

                // Excel 파일 저장
                try (FileOutputStream fileOut = new FileOutputStream("C:/output/send/decl/Declaration_Rev_"+Warant+"_"+date+".xlsx")) {
                    workbook.write(fileOut);

                    declDTO.setSEND_FILE_PATH("C:/output/send/decl_files/");
                    declDTO.setSEND_FILE_NAME("Declaration_Rev_"+Warant+"_"+date+".xlsx");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Excel 파일이 생성되었습니다.");

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }else{
            try {
                if (declFile != null && !declFile.isEmpty()) {
                    // 1. 파일 저장 경로와 파일명 설정
                    String uploadDir = "C:/output/send/decl_files"; // 저장할 디렉터리
//                    String fileName = "custom_name_" + System.currentTimeMillis() + "."
//                            + getFileExtension(declFile.getOriginalFilename()); // 새로운 파일명 생성
                    String fileName = "Declaration_Rev_"+Warant+"_"+date+".xlsx";

                    File directory = new File(uploadDir);
                    if (!directory.exists()) {
                        directory.mkdirs(); // 디렉터리가 없으면 생성
                    }

                    // 2. 파일 저장
                    File destinationFile = new File(directory, fileName);
                    declFile.transferTo(destinationFile);

                    // 3. 저장된 파일 정보 처리
                    System.out.println("파일이 저장되었습니다: " + destinationFile.getAbsolutePath());

                    // 필요하면 declDTO에 파일 경로나 이름 저장
                    declDTO.setSEND_FILE_PATH(destinationFile.getAbsolutePath());
                    declDTO.setSEND_FILE_NAME(fileName);

                    //return ResponseEntity.ok("파일 저장 성공: " + fileName);
                } else {
                    //return ResponseEntity.badRequest().body("DECL_FILE이 비어 있습니다.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("파일 저장 중 오류 발생: " + e.getMessage());
            }
        }




        try {

            String DECL_IDX = GetParam(request, "DECL_IDX", "");

            if(declFile != null){
                if (!declFile.isEmpty()) {
                    String decl_filepath = partMgmtService.uploadFileData(PM_PART_CODE, declFile);
                    String decl_filename = declFile.getOriginalFilename();

                    declDTO.setFILE_NAME(decl_filename);
                    declDTO.setFILE_PATH(decl_filepath);

                }
            }

            log.info("DECL_IDX=================="+DECL_IDX);
            if (DECL_IDX == "") {
                //신규
                result += partMgmtService.insertDeclData(declDTO);
            } else {
                //기존
                result += partMgmtService.updateDeclData(declDTO);
            }

        } catch (Exception e) {
            log.info("ERROR====================="+ e.getMessage());

            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        log.info("flag_Lase================"+flag);

        if(flag.equals("Save")) {
            return ResponseEntity.ok("OK");
        }else{
            return ResponseEntity.ok("NEXT|||"+PM_IDX);
        }

    }



    /*********************************************************************************************************************
     ** Detail v페이지
     ** SCCS / 성분명세서 / 기타보증 페이지 **
     *********************************************************************************************************************/


    @RequestMapping("/goSccsDetail")
    public String partMgmSccsDetail(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        try {

            String idx = GetParam(request, "PM_IDX", "");
            model.addAttribute("PM_IDX", idx);

            PartMgmtDTO partMgmtDTO = partMgmtService.getPartData(idx);
            if (partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
            model.addAttribute("partMgmtDTO", partMgmtDTO);
            log.info("partMgmtDTO=============================" + partMgmtDTO);

            //데이터가 있다면 들고와서 뿌려줌
            //SCCS
            partDetailSccsDTO sccsDTO = partMgmtService.getSccsData(idx);
            if (sccsDTO == null) sccsDTO = new partDetailSccsDTO();
            model.addAttribute("sccsDTO", sccsDTO);
            log.info("sccsDTO=============================" + sccsDTO);
            //성분명세서
            partDetailIngredDTO ingredDTO = partMgmtService.getIngredData(idx);
            if (ingredDTO == null) ingredDTO = new partDetailIngredDTO();
            model.addAttribute("ingredDTO", ingredDTO);
            log.info("ingredDTO=============================" + ingredDTO);
            //ETC
            List<partDetailGuarantDTO> guarantListDTO =partMgmtService.getGuarantData(idx);
            if(guarantListDTO == null) {
                partDetailGuarantDTO guarantDTO = new partDetailGuarantDTO();
                guarantListDTO.add(guarantDTO);
                model.addAttribute("GUARANT_COUNT",0);
            }else{
                model.addAttribute("GUARANT_COUNT",guarantListDTO.size());
            }

            model.addAttribute("guarantListDTO",guarantListDTO);
            log.info("guarantListDTO=============================" + guarantListDTO);

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
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||" + e.getMessage());
                    printer.close();
                } catch (Exception e2) {
                }

                //return  "redirect:/main";
                return "blank";
            }
        }

        return "partMgmtList/partMgmDetailSccs";
        //return "partMgmtList/detail";


    }

    @RequestMapping("/goSccsSave")
    public ResponseEntity<?> partMgmtSccsDetailSave(Model model, HttpServletRequest request
                                                    , HttpServletResponse response
                                                    , @RequestParam(value = "SCCS_FILE", required = false) MultipartFile sccsFile
                                                    , @RequestParam(value = "INGRED_FILE", required = false) MultipartFile ingredFile
                                                    , @ModelAttribute partDetailSccsDTO sccsDTO
                                                    , @ModelAttribute partDetailIngredDTO ingredGDTO
                                                    , @AuthenticationPrincipal UserCustom user) {

        int result = 0;
        String PM_PART_CODE = GetParam(request, "PM_PART_CODE", "");
        int PM_IDX = Integer.parseInt(GetParam(request, "PM_IDX", "0"));
        String flag = GetParam(request, "SaveMode","");
        log.info("flag================"+flag);

        try {

            //SCCS
//        String saveMode = GetParam(request,"SaveMode","");
//
            String SCCS_IDX = GetParam(request, "SCCS_IDX","");

            //msds file
            //if (!msdsFile.isEmpty()) {
            if(sccsFile != null){
                if (!sccsFile.isEmpty()) {
                    String etc_filepath = partMgmtService.uploadFileData(PM_PART_CODE, sccsFile);
                    String etc_filename = sccsFile.getOriginalFilename();

                    sccsDTO.setSCCS_FILE_NAME(etc_filename);
                    sccsDTO.setSCCS_FILE_PATH(etc_filepath);

                }
            }

            log.info("SCCS_IDX=================="+SCCS_IDX);
            if (SCCS_IDX == "") {
                //신규
                result += partMgmtService.insertSccsData(sccsDTO);
            } else {
                //기존
                result += partMgmtService.updateSccsData(sccsDTO);
            }


            //Ingredent
            String INGRED_IDX = GetParam(request, "INGRED_IDX", "");


            //Ingredent file
            //if (!rohsFile.isEmpty()) {
            if (ingredFile != null) {
                String etc_filepath = partMgmtService.uploadFileData(PM_PART_CODE, ingredFile);
                String etc_filename = ingredFile.getOriginalFilename();

                ingredGDTO.setINGRED_FILE_NAME(etc_filename);
                ingredGDTO.setINGRED_FILE_PATH(etc_filepath);

            }


            if (INGRED_IDX == "") {
                //신규
                result += partMgmtService.insertIngredData(ingredGDTO);
            } else {
                //기존
                result += partMgmtService.updateIngredData(ingredGDTO);
            }



            //etc
            int guarantCount = Integer.parseInt(GetParam(request, "guarantCount", "0"));
            //String ETC_IDX = GetParam(request, "ETC_IDX", "");
            log.info("test00000============"+result+"==========PM_IDX : "+PM_IDX);

            //삭제된 etc 데이터 db에서 삭제
            String GUR_DEL_IDX = GetParam(request,"GUR_DEL_IDX","");
            String[] ArrDelIdx = GUR_DEL_IDX.split(",");
            for(int j =0; j< ArrDelIdx.length; j++) {
                if(!ArrDelIdx[j].equals("") ){
                    partMgmtService.deleteGuarantData(Integer.parseInt(ArrDelIdx[j]));
                }
            }


            //List<partDetailGuarantDTO> guarantDTOList = new ArrayList<>();
            for (int i = 0; i < guarantCount; i++) {
                if(request.getParameter("GUARANT_IDX"+(i+1)) != null) {
                    partDetailGuarantDTO guarantDTO = new partDetailGuarantDTO();
                    guarantDTO.setGUARANT_IDX(GetParam(request, "GUARANT_IDX" + (i+1), ""));
                    guarantDTO.setPM_IDX(GetParam(request, "PM_IDX" , ""));
                    guarantDTO.setGUARANT_TYPE(GetParam(request, "GUARANT_TYPE" + (i+1), ""));
                    guarantDTO.setGUARANT_CONFIRM_DATE(GetParam(request, "GUARANT_CONFIRM_DATE" + (i+1), ""));

                    MultipartFile files = ((StandardMultipartHttpServletRequest) request).getFile("GUARANT_FILE" + (i+1));

                    if(files != null){
//                    if(guarantFile != null){
//                        if (!guarantFile[i].isEmpty()) {
                            //MultipartFile files = guarantFile[i];

                            String etc_filepath = partMgmtService.uploadFileData(PM_PART_CODE, files);
                            String etc_filename = files.getOriginalFilename();

                            guarantDTO.setGUARANT_FILE_NAME(etc_filename);
                            guarantDTO.setGUARANT_FILE_PATH(etc_filepath);

                        //}
                    }

                    if(GetParam(request, "GUARANT_IDX" + (i+1), "").equals("")) {
                        //신규
                        result += partMgmtService.insertGuarantData(guarantDTO);
                    }else {
                        result += partMgmtService.updateGuarantData(guarantDTO);
                    }

                }

            }
            log.info("test1111111============"+result+"==========PM_IDX : "+PM_IDX);
        } catch (Exception e) {
            log.info("ERROR====================="+ e.getMessage());

            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        log.info("flag_Lase================"+flag);

        if(flag.equals("Save")) {
            return ResponseEntity.ok("OK");
        }else{
            //승인요청
            partMgmtService.updateApprovalStatus(PM_IDX, "3");
            //체크 데이터 저장
            //partMgmtService.saveConfirmChkData();
            //히스토리 데이터 저장, 해당내용은 이후 보증내역 유무에 사용함
            int userIdx = user.getUSER_IDX();
            partMgmtService.setHistoryData(PM_IDX,userIdx,"APPROVAL");
            //체크 init
            partMgmtService.initConfirmChk(PM_IDX);
            return ResponseEntity.ok("NEXT|||"+PM_IDX);
        }

    }


    @GetMapping("/partMgmtFileDownIdx")
    public ResponseEntity<Resource> partMgmtDownloadFile(HttpServletRequest request) throws Exception {
        int idx = Integer.parseInt(GetParam(request,"IDX","0"));
        String gubun = GetParam(request,"GUBUN","");
        Map<String,String> map = new HashMap<>();
        String path = "";
        String name = "";

        if(gubun.equals("MSDS")){
            map = partMgmtService.getMsdsFileData(idx);

            path = map.get("FILE_PATH");
            name = map.get("FILE_NAME");

        }else if(gubun.equals("ROHS")){
            map = partMgmtService.getRohsFileData(idx);

            path = map.get("FILE_PATH");
            name = map.get("FILE_NAME");
        }else if(gubun.equals("HALOGEN")){
            map = partMgmtService.getHalgFileData(idx);

            path = map.get("FILE_PATH");
            name = map.get("FILE_NAME");
        }else if(gubun.equals("ETC")){
            map = partMgmtService.getEtcFileData(idx);

            path = map.get("FILE_PATH");
            name = map.get("FILE_NAME");

        }else if(gubun.equals("SVHC")){
            map = partMgmtService.getDetailSvhcFileData(idx);

            path = map.get("FILE_PATH");
            name = map.get("FILE_NAME");

        }else if(gubun.equals("DECL")){
            map = partMgmtService.getDetailDeclFileData(idx);

            path = map.get("FILE_PATH");
            name = map.get("FILE_NAME");

        }else if(gubun.equals("SCCS")){
            map = partMgmtService.getSccsFileData(idx);

            path = map.get("FILE_PATH");
            name = map.get("FILE_NAME");

        }else if(gubun.equals("INGRED")){
            map = partMgmtService.getIngredFileData(idx);

            path = map.get("FILE_PATH");
            name = map.get("FILE_NAME");

        }else if(gubun.equals("GUARANT")){
            map = partMgmtService.getGuarantDataFileData(idx);

            path = map.get("FILE_PATH");
            name = map.get("FILE_NAME");
        }

        FileSystemResource resource2 = new FileSystemResource(path+name);

        if (!resource2.exists() || !resource2.isReadable()) {
            throw new Exception("File not found or not readable: " + name);
        }

        Path file = Paths.get(path).resolve(name).normalize();
        Resource resource = new UrlResource(file.toUri());

        if(resource.exists() && resource.isReadable()){
            String encodedFileName = URLEncoder.encode(resource.getFilename(),"UTF-8").replace("+","%20");
            String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition)
                    .body(resource);

        }else{
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            return ResponseEntity.notFound().build();
        }
    }



    //파일명 인코딩
    private String encodeFileName(String filename) throws UnsupportedEncodingException {
        // Encode filename in UTF-8
        String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");

        // Ensure that non-ASCII characters are correctly encoded
        return encodedFilename;
    }

    private String GetParam(HttpServletRequest request, String pName, String pDefault) {
        String ParamValue = request.getParameter(pName);

        if (ParamValue == null || ParamValue.isEmpty()) {
            ParamValue = pDefault;
        };

        return ParamValue;
    }

    // 파일 확장자 추출 메서드
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }


    /******************************************************************************
     *
     ****************************************************************************/
    private String compareMsdsData(partDetailMsdsDTO dto, partDetailMsdsDTO origDto) {
        StringBuilder differences = new StringBuilder();

        if (!Objects.equals(dto.getMSDS_REG_DATE(), origDto.getMSDS_REG_DATE())) {
            differences.append("MSDS_REG_DATE|");
        }
        if (!Objects.equals(dto.getMSDS_LANG(), origDto.getMSDS_LANG())) {
            differences.append("MSDS_LANG|");
        }
        if (!Objects.equals(dto.getMSDS_APPROVAL_NUM(), origDto.getMSDS_APPROVAL_NUM())) {
            differences.append("MSDS_APPROVAL_NUM|");
        }
        if (!Objects.equals(dto.getMSDS_FILE_PATH(), origDto.getMSDS_FILE_PATH()) && !Objects.equals(dto.getMSDS_FILE_NAME(), origDto.getMSDS_FILE_NAME())) {
            differences.append("MSDS_FILE|");
        }

        return differences.toString();
    }

//    private String compareRohsData(partDetailRohsDTO dto, partDetailRohsDTO origDto) {
//        StringBuilder differences = new StringBuilder();
//
//        if (!Objects.equals(dto.getrohs, origDto.getMSDS_REG_DATE())) {
//            differences.append("MSDS File Name differs: ")
//                    .append(dto.getMsdsFileName()).append(" vs ").append(origDto.getMsdsFileName()).append("\n");
//        }
//        if (!Objects.equals(dto.getMSDS_LANG(), origDto.getMSDS_LANG())) {
//            differences.append("MSDS File Path differs: ")
//                    .append(dto.getMsdsFilePath()).append(" vs ").append(origDto.getMsdsFilePath()).append("\n");
//        }
//        if (!Objects.equals(dto.getMSDS_APPROVAL_NUM(), origDto.getMSDS_APPROVAL_NUM())) {
//            differences.append("MSDS Language differs: ")
//                    .append(dto.getMsdsLang()).append(" vs ").append(origDto.getMsdsLang()).append("\n");
//        }
//
//        return differences.toString();
//    }
//
//    private String compareHalgData(partDetailHalGDTO dto, partDetailHalGDTO origDto) {
//        StringBuilder differences = new StringBuilder();
//
//        if (!Objects.equals(dto.getMSDS_REG_DATE(), origDto.getMSDS_REG_DATE())) {
//            differences.append("MSDS File Name differs: ")
//                    .append(dto.getMsdsFileName()).append(" vs ").append(origDto.getMsdsFileName()).append("\n");
//        }
//        if (!Objects.equals(dto.getMSDS_LANG(), origDto.getMSDS_LANG())) {
//            differences.append("MSDS File Path differs: ")
//                    .append(dto.getMsdsFilePath()).append(" vs ").append(origDto.getMsdsFilePath()).append("\n");
//        }
//        if (!Objects.equals(dto.getMSDS_APPROVAL_NUM(), origDto.getMSDS_APPROVAL_NUM())) {
//            differences.append("MSDS Language differs: ")
//                    .append(dto.getMsdsLang()).append(" vs ").append(origDto.getMsdsLang()).append("\n");
//        }
//
//        return differences.toString();
//    }


    public static String compareObjects(Object newDto, Object originDto) {
        StringBuilder differences = new StringBuilder();

        // 두 객체가 같은 클래스인지 확인
        if (!newDto.getClass().equals(originDto.getClass())) {
            return "Objects are of different types.";
        }

        // 클래스의 필드 가져오기
        Field[] fields = newDto.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // private 필드 접근 가능 설정

            try {
                Object value1 = field.get(newDto);
                Object value2 = field.get(originDto);

                // INFO_FLAG 필드는 제외
                if ("INFO_FLAG".equals(field.getName())) {
                    continue;
                }
                if ("CONFIRM_CHK".equals(field.getName()) || field.getName().endsWith("CONFIRM_CHK")) {
                    //if(value1.equals("DEL"))
                    continue;
                }
                // 첨부파일은 따로하자
                if ("FILE_STATUS".equals(field.getName()) || field.getName().endsWith("FILE_STATUS")) {
                    //if(value1.equals("DEL"))
                    continue;
                }
//                if ("FILE_NAME".equals(field.getName()) || field.getName().endsWith("FILE_NAME")) {
//                    //if(value1.equals("DEL"))
//                    continue;
//                }
//                if ("FILE_PATH".equals(field.getName()) || field.getName().endsWith("FILE_PATH")) {
//                    //if(value1.equals("DEL"))
//                    continue;
//                }

                // 값 비교
                if (!Objects.equals(value1, value2)) {
                    differences.append(field.getName()).append(": ")
                            .append(value1).append(",").append(value2).append("|");
                }
            } catch (IllegalAccessException e) {
                differences.append("Error accessing field '").append(field.getName()).append("': ")
                        .append(e.getMessage()).append("\n");
            }
        }

        return differences.toString().isEmpty() ? "" : differences.toString();
    }

}
