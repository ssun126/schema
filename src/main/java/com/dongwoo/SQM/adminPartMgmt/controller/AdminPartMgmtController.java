package com.dongwoo.SQM.adminPartMgmt.controller;

import com.dongwoo.SQM.adminPartMgmt.dto.AdminPartMgmtDTO;
import com.dongwoo.SQM.adminPartMgmt.service.AdminPartMgmtService;
import com.dongwoo.SQM.common.dto.ExpirationDateDTO;
import com.dongwoo.SQM.common.service.ExpirationDataService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.system.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping("/admin/partMgmt/approvalState")
    public String PartMgmtList(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        List<HashMap> searchPlantList = partMgmtService.getPlantList();
        model.addAttribute("searchPlantList",searchPlantList);

        try {
            ObjectMapper mapper = new ObjectMapper();

            String sComCode = GetParam(request, "sComCode", "");
            String sComName = GetParam(request, "sComName", "");
            String sPartCode = GetParam(request, "sPartCode", "");
            String sPartName = GetParam(request, "sPartName", "");
            String sRegUser = GetParam(request, "sRegUser", "");
            String sUseYN = GetParam(request, "sUseYN", "");
            String sPlant = GetParam(request, "sPlant", "");
            String sApproval = GetParam(request, "sApproval", "");
            String sStartDate = GetParam(request, "sStartDate", "");
            String sEndDate = GetParam(request, "sEndDate", "");

            model.addAttribute("sComCode", sComCode);
            model.addAttribute("sComName", sComName);
            model.addAttribute("sPartCode", sPartCode);
            model.addAttribute("sPartName", sPartName);
            model.addAttribute("sRegUser", sRegUser);
            model.addAttribute("sUseYN", sUseYN);
            model.addAttribute("sPlant", sPlant);
            model.addAttribute("sApproval", sApproval);
            model.addAttribute("sStartDate", sStartDate);
            model.addAttribute("sEndDate", sEndDate);

            AdminPartMgmtDTO parmDTO = new AdminPartMgmtDTO();
            parmDTO.setCOM_CODE(sComCode);
            parmDTO.setCOM_NAME(sComName);
            parmDTO.setPM_PART_CODE(sPartCode);
            parmDTO.setPART_NAME(sPartName);
            parmDTO.setSEARCH_REG_USER(sRegUser);
            parmDTO.setPM_PART_PLANT_CODE(sPlant);
            parmDTO.setPM_ACTIVE_YN(sUseYN);
            parmDTO.setPM_APPROVAL_STATUS(sApproval);
            parmDTO.setSEARCH_PM_SDATE(sStartDate);
            parmDTO.setSEARCH_PM_EDATE(sEndDate);

            List<AdminPartMgmtDTO> partMgmtDTOList = adminPartMgmtService.searchAdminPartMgmt(parmDTO);
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

        return "approvalState/adminMain";
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

            List<HashMap> partMSDSList = adminPartMgmtService.getPartMSDSExpList(EXP_DATE, COM_CODE, COM_NAME,expirationDatDTO.getEXP_MONTH());
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

    @PostMapping("/admin/partMgmt/expDateRohsSave")
    public ResponseEntity<?> expDateRohsSave(HttpServletRequest request, HttpSession session) {
        try {
            int RoHS_EXP_MONTH = Integer.parseInt(GetParam(request, "EXP_ROSH", "0"));
            int Halogen_EXP_MONTH = Integer.parseInt(GetParam(request, "EXP_HALG", "0"));
//            String EXP_ROSH = GetParam(request, "EXP_ROSH", "");
//            String EXP_HALG = GetParam(request, "EXP_HALG", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loginId = user.getUsername();
            UserMgrDTO memberDTO = memberService.findByMemberId(loginId); //동우 사용자

            expirationDateService.setExpiration("F", 2, "RoHS", RoHS_EXP_MONTH, null, memberDTO.getUSER_IDX());
            expirationDateService.setExpiration("F", 2, "Halogen", Halogen_EXP_MONTH, null, memberDTO.getUSER_IDX());
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
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

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<HashMap> partSvhdList = adminPartMgmtService.getPartSvhcExpList();
            String partSvhcListStr = mapper.writeValueAsString(partSvhdList);

            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(partSvhcListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }
            model.addAttribute("partSvhcLogList",partSvhcListStr);
        } catch (JsonProcessingException e) {
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

        return "expDateSvhc/main";
    }

    @PostMapping("/admin/partMgmt/expBodySvhcSave")
    public ResponseEntity<?> expBodySvhcSave(HttpServletRequest request, HttpSession session) {
        try {
            String EXP_BODY = GetParam(request, "EXP_BODY", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loginId = user.getUsername();
            UserMgrDTO memberDTO = memberService.findByMemberId(loginId); //동우 사용자

            expirationDateService.setExpiration("F", 2, "SVHC", 0, EXP_BODY, memberDTO.getUSER_IDX());
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/admin/partMgmt/sendSvhcExpAlert")
    public ResponseEntity<?> sendSvhcExpAlert(HttpServletRequest request, HttpSession session,@AuthenticationPrincipal UserCustom user) {
        try {
            String EXP_BODY = GetParam(request, "EXP_BODY", "");

            //(CODE1, CODE2, CODE3, MES_KIND,GUBN,KIND,SEND_TYPE ,SEND_FROM ,SEND_TO, SEND_TITLE , SEND_BODY , SEND_DATE ,REG_DW_USER_IDX , REG_COM_USER_IDX  )
            //VALUES ('F',2,(SELECT COUNT(*) FROM  SC_MESSAGE_LOG WHERE CODE1 ='F' AND CODE2 =2 )+1,'SVHC','Part 갱신','Part Mana','Email','Schema','공급사', 'SVHC 확인서 갱신','test',sysdate,0,0)
            log.info("test111111=="+EXP_BODY);
            log.info(", user.getUSER_IDX()========="+user.getUSER_IDX());
            log.info(", user.getCOM_USER_IDX()========="+user.getCOM_USER_IDX());
            expirationDateService.sendExpAlert("F", 2, "SVHC","Part 갱신","Part Mana","Email","Schema","공급사", "SVHC 확인서 갱신", EXP_BODY, user.getUSER_IDX(),user.getCOM_USER_IDX());
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
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

        try {
            ObjectMapper mapper = new ObjectMapper();

            String EXP_DATE = GetParam(request, "EXP_DATE", "");
            String COM_CODE = GetParam(request, "COM_CODE", "");
            String COM_NAME = GetParam(request, "COM_NAME", "");

            model.addAttribute("EXP_DATE",EXP_DATE);
            model.addAttribute("COM_CODE",COM_CODE);
            model.addAttribute("COM_NAME",COM_NAME);

            List<HashMap> partDeclList = adminPartMgmtService.getPartDeclExpList(EXP_DATE, COM_CODE, COM_NAME,expirationDatDTO.getEXP_MONTH());
            String partDeclListStr = mapper.writeValueAsString(partDeclList);

            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
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
                return  "redirect:/main";
            }
        }

        return "expDateDecl/main";
    }

    @PostMapping("/admin/partMgmt/expDateDeclSave")
    public ResponseEntity<?> expDateDeclSave(HttpServletRequest request, HttpSession session) {
        try {
            int EXP_MONTH = Integer.parseInt(GetParam(request, "EXP_MONTH", "0"));
            String EXP_BODY = GetParam(request, "EXP_BODY", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loginId = user.getUsername();
            UserMgrDTO memberDTO = memberService.findByMemberId(loginId); //동우 사용자

            expirationDateService.setExpiration("F", 2, "Declaration", EXP_MONTH, EXP_BODY, memberDTO.getUSER_IDX());
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
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

        try {
            ObjectMapper mapper = new ObjectMapper();

            String EXP_DATE = GetParam(request, "EXP_DATE", "");
            String COM_CODE = GetParam(request, "COM_CODE", "");
            String COM_NAME = GetParam(request, "COM_NAME", "");

            model.addAttribute("EXP_DATE",EXP_DATE);
            model.addAttribute("COM_CODE",COM_CODE);
            model.addAttribute("COM_NAME",COM_NAME);

            List<HashMap> partEtcList = adminPartMgmtService.getPartEtcExpList(EXP_DATE, COM_CODE, COM_NAME,expirationDatDTO.getEXP_MONTH());
            String partEtcListStr = mapper.writeValueAsString(partEtcList);

            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(partEtcListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("partEtcList",partEtcListStr);
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

        return "expDateEtc/main";
    }

    @PostMapping("/admin/partMgmt/expDateEtcSave")
    public ResponseEntity<?> expDateEtcSave(HttpServletRequest request, HttpSession session) {
        try {
            int EXP_MONTH = Integer.parseInt(GetParam(request, "EXP_MONTH", "0"));
            String EXP_BODY = GetParam(request, "EXP_BODY", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loginId = user.getUsername();
            UserMgrDTO memberDTO = memberService.findByMemberId(loginId); //동우 사용자

            expirationDateService.setExpiration("F", 2, "PART_ETC", EXP_MONTH, EXP_BODY, memberDTO.getUSER_IDX());
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
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
