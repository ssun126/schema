package com.dongwoo.SQM.adminPartMgmt.controller;

import com.dongwoo.SQM.adminPartMgmt.dto.AdminPartMgmtDTO;
import com.dongwoo.SQM.adminPartMgmt.service.AdminPartMgmtService;
import com.dongwoo.SQM.common.dto.ExpirationDateDTO;
import com.dongwoo.SQM.common.service.ExpirationDataService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.system.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminPartMgmtController {

    private final PartMgmtService partMgmtService;
    private final AdminPartMgmtService adminPartMgmtService;
    private final ExpirationDataService expirationDateService;
    private final MemberService memberService;

    @GetMapping("/admin/partMgmt/approvalState")
    public String PartMgmtList(Model model) {
        //바인딩 리스트
        //검색 basecode 취급플랜트
        List<HashMap> searchPlantList = partMgmtService.getPlantList();
        //검색 basecode 승인현황
        //List<HashMap> searhApprovalStatus = partMgmtService.getApprovalStatus();

        model.addAttribute("searchPlantList",searchPlantList);
        //model.addAttribute("searhApprovalStatus", searhApprovalStatus);

        return "approvalState/adminMain";
    }

    @GetMapping("/admin/partMgmt/searchAdminPartMgmt")
    public ResponseEntity<?> searchAdminPartMgmt(@RequestParam("code") String code, @RequestParam("name") String name, @RequestParam("reguser") String reguser,
                                            @RequestParam("useyn") String useyn, @RequestParam("plant") String plant, @RequestParam("approval") String approval,
                                            @RequestParam("sdate") String sdate, @RequestParam("edate") String edate){
        //ist<PartMgmtDTO> partMgmtDTOList = PartMgmtService.searchPartMgmt(code,name,reguser,useyn,plant,approval,sdate,edate);
        try{
            AdminPartMgmtDTO parmDTO = new AdminPartMgmtDTO();
            parmDTO.setPM_PART_CODE(code);
            parmDTO.setPM_PART_NAME(name);
            parmDTO.setREG_USER(reguser);
            parmDTO.setPM_PLANT(plant);
            parmDTO.setPM_ACTIVE_YN(useyn);
            parmDTO.setPM_APPROVAL_STATUS(approval);
            parmDTO.setPM_SDATE(sdate);
            parmDTO.setPM_EDATE(edate);

            List<AdminPartMgmtDTO> partMgmtDTOList = adminPartMgmtService.searchAdminPartMgmt(parmDTO);
            log.info("dataaaaaaaa??????????????"+partMgmtDTOList);
            return ResponseEntity.ok(partMgmtDTOList);

        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }



    @GetMapping("/admin/partMgmt/approvalStateDetail")
    public String PartMgmtDetail(Model model) {
        return "approvalState/detail";
    }


    /*******************************************************************************************************************************************/
    /**
     * 만료일 관리 MSDS
     */
    @RequestMapping("/admin/partMgmt/expDateMsds")
    public String expDateMsdsList(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        ExpirationDateDTO expirationDatDTO = expirationDateService.getExpiration("F", 2, "MSDS");
        model.addAttribute("EXP_MONTH",expirationDatDTO.getEXP_MONTH());
        model.addAttribute("EXP_BODY",expirationDatDTO.getEXP_BODY());

        try {
            ObjectMapper mapper = new ObjectMapper();

            String EXP_DATE = GetParam(request, "EXP_DATE", "");
            String COM_CODE = GetParam(request, "COM_CODE", "");
            String COM_NAME = GetParam(request, "COM_NAME", "");

            model.addAttribute("EXP_DATE",EXP_DATE);
            model.addAttribute("COM_CODE",COM_CODE);
            model.addAttribute("COM_NAME",COM_NAME);

            List<HashMap> partMSDSList = adminPartMgmtService.getPartMSDSExpList(EXP_DATE, COM_CODE, COM_NAME);
            String partMSDSListStr = mapper.writeValueAsString(partMSDSList);

            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(partMSDSListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("partMSDSList",partMSDSListStr);
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

        return "expDateMsds/main";
    }

    @PostMapping("/admin/partMgmt/expDateMsdsSave")
    public ResponseEntity<?> expDateMsdsSave(HttpServletRequest request, HttpSession session) {
        try {
            int EXP_MONTH = Integer.parseInt(GetParam(request, "EXP_MONTH", "0"));
            String EXP_BODY = GetParam(request, "EXP_BODY", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loginId = user.getUsername();
            UserMgrDTO memberDTO = memberService.findByMemberId(loginId); //동우 사용자

            expirationDateService.setExpiration("F", 2, "MSDS", EXP_MONTH, EXP_BODY, memberDTO.getUSER_IDX());
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }
    /*******************************************************************************************************************************************/

    /******************************************************************************************************************************************/
    /**
     * 만료일 관리 RoHSⅡ/Halogen
     */
    @RequestMapping("/admin/partMgmt/expDateRohs")
    public String expDateRohsList(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        ExpirationDateDTO expirationDatDTO_RoHS = expirationDateService.getExpiration("F", 2, "RoHS");
        model.addAttribute("RoHS_EXP_MONTH",expirationDatDTO_RoHS.getEXP_MONTH());
        ExpirationDateDTO expirationDatDTO_Halogen = expirationDateService.getExpiration("F", 2, "Halogen");
        model.addAttribute("Halogen_EXP_MONTH",expirationDatDTO_Halogen.getEXP_MONTH());

        return "expDateRohs/main";
    }
    /*******************************************************************************************************************************************/

    /*******************************************************************************************************************************************/
    /**
     * 만료일 관리 SVHC
     */
    @RequestMapping("/admin/partMgmt/expDateSvhc")
    public String expDateSvhcList(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        ExpirationDateDTO expirationDatDTO = expirationDateService.getExpiration("F", 2, "SVHC");
        model.addAttribute("EXP_BODY",expirationDatDTO.getEXP_BODY());

        return "expDateSvhc/main";
    }
    /*******************************************************************************************************************************************/

    /*******************************************************************************************************************************************/
    /**
     * 만료일 관리 Declaration Letter
     */
    @RequestMapping("/admin/partMgmt/expDateDecl")
    public String expDateDeclList(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        ExpirationDateDTO expirationDatDTO = expirationDateService.getExpiration("F", 2, "Declaration");
        model.addAttribute("EXP_MONTH",expirationDatDTO.getEXP_MONTH());
        model.addAttribute("EXP_BODY",expirationDatDTO.getEXP_BODY());

        return "expDateDecl/main";
    }
    /*******************************************************************************************************************************************/

    /*******************************************************************************************************************************************/
    /**
     * 만료일 관리 기타보증서류
     */
    @RequestMapping("/admin/partMgmt/expDateEtc")
    public String expDateEtcList(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        ExpirationDateDTO expirationDatDTO = expirationDateService.getExpiration("F", 2, "PART_ETC");
        model.addAttribute("EXP_MONTH",expirationDatDTO.getEXP_MONTH());
        model.addAttribute("EXP_BODY",expirationDatDTO.getEXP_BODY());

        return "expDateEtc/main";
    }
    /*******************************************************************************************************************************************/

    private String GetParam(HttpServletRequest request, String pName, String pDefault) {
        String ParamValue = request.getParameter(pName);

        if (ParamValue == null || ParamValue.isEmpty()) {
            ParamValue = pDefault;
        };

        return ParamValue;
    }
}
