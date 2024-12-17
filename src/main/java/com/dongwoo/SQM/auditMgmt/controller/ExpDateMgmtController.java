package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import com.dongwoo.SQM.auditMgmt.service.LabourHRService;
import com.dongwoo.SQM.auditMgmt.service.SafetyHealthService;
import com.dongwoo.SQM.common.dto.ExpirationDateDTO;
import com.dongwoo.SQM.common.service.ExpirationDataService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.system.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class ExpDateMgmtController {
    @Autowired
    private IsoAuthService isoAuthService;
    @Autowired
    private LabourHRService labourHRService;
    @Autowired
    private AuditCommonService auditCommonService;
    private final ExpirationDataService expirationDateService;
    private final MemberService memberService;

    // admin -만료일 - 업체별 ISO 인증서 정보
    @RequestMapping("/admin/auditMgmt/expDateIso")
    public String searchExpDateIsoAuth(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String EXP_DATE = GetParam(request, "EXP_DATE", "");
            String COM_CODE = GetParam(request, "COM_CODE", "");
            String COM_NAME = GetParam(request, "COM_NAME", "");

            model.addAttribute("EXP_DATE",EXP_DATE);
            model.addAttribute("COM_CODE",COM_CODE);
            model.addAttribute("COM_NAME",COM_NAME);

            // 검색 조건에 맞는 결과를 반환
            List<HashMap> isoAuthList = isoAuthService.getExpDateList(COM_CODE,COM_NAME,EXP_DATE);
            String isoAuthListStr = mapper.writeValueAsString(isoAuthList);
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(isoAuthListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("isoAuthList",isoAuthListStr);
        } catch (Exception e) {
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||" + e.getMessage());
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            } else {
                return  "redirect:/main";
            }
        }
        return "expDateIso/main";
    }

    // admin -노동환경 만료일 - 업체별
    @RequestMapping("/admin/auditMgmt/expDateLabour")
    public String searchExpDateLabour(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        ExpirationDateDTO expirationDatDTO = expirationDateService.getExpiration("E", 1, "LABOUR");
        model.addAttribute("EXP_MONTH",expirationDatDTO.getEXP_MONTH());
        model.addAttribute("EXP_BODY",expirationDatDTO.getEXP_BODY());

        try {
            ObjectMapper mapper = new ObjectMapper();

            String COM_CODE = GetParam(request, "COM_CODE", "");
            String COM_NAME = GetParam(request, "COM_NAME", "");

            model.addAttribute("COM_CODE",COM_CODE);
            model.addAttribute("COM_NAME",COM_NAME);

            // 검색 조건에 맞는 결과를 반환
            List<HashMap> auditList = auditCommonService.getExpDateList(COM_CODE,COM_NAME,"LABOUR");
            String auditListStr = mapper.writeValueAsString(auditList);
            log.info("auditListStr::::::::::::::::::::::"+auditList);
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(auditListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("auditList",auditListStr);
        } catch (Exception e) {
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||" + e.getMessage());
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            } else {
                return  "redirect:/main";
            }
        }
        return "expDateLabour/main";
    }

    //노동환경 만료일 설정
    @PostMapping("/admin/auditMgmt/expDateLabourSave")
    public ResponseEntity<?> expDateLabourSave(HttpServletRequest request, HttpSession session) {
        try {
            int EXP_MONTH = Integer.parseInt(GetParam(request, "EXP_MONTH", "0"));
            String EXP_BODY = GetParam(request, "EXP_BODY", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loginId = user.getUsername();
            UserMgrDTO memberDTO = memberService.findByMemberId(loginId); //동우 사용자

            expirationDateService.setExpiration("E", 1, "LABOUR", EXP_MONTH, EXP_BODY, memberDTO.getUSER_IDX());
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }


    // admin -안전보건 만료일 - 업체별
    @RequestMapping("/admin/auditMgmt/expDateSafety")
    public String searchExpDateSafety(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        ExpirationDateDTO expirationDatDTO = expirationDateService.getExpiration("E", 1, "SAFETY");
        model.addAttribute("EXP_MONTH",expirationDatDTO.getEXP_MONTH());
        model.addAttribute("EXP_BODY",expirationDatDTO.getEXP_BODY());
        try {
            ObjectMapper mapper = new ObjectMapper();

            String COM_CODE = GetParam(request, "COM_CODE", "");
            String COM_NAME = GetParam(request, "COM_NAME", "");

            model.addAttribute("COM_CODE",COM_CODE);
            model.addAttribute("COM_NAME",COM_NAME);

            // 검색 조건에 맞는 결과를 반환
            List<HashMap> auditList = auditCommonService.getExpDateList(COM_CODE,COM_NAME,"SAFETY");
            String auditListStr = mapper.writeValueAsString(auditList);
            log.info("auditListStr::::::::::::::::::::::"+auditList);
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(auditListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("auditList",auditListStr);
        } catch (Exception e) {
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||" + e.getMessage());
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            } else {
                return  "redirect:/main";
            }
        }
        return "expDateSafety/main";
    }

    //안전보건 만료일 설정
    @PostMapping("/admin/auditMgmt/expDateSafetySave")
    public ResponseEntity<?> expDateSafetySave(HttpServletRequest request, HttpSession session) {
        try {
            int EXP_MONTH = Integer.parseInt(GetParam(request, "EXP_MONTH", "0"));
            String EXP_BODY = GetParam(request, "EXP_BODY", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loginId = user.getUsername();
            UserMgrDTO memberDTO = memberService.findByMemberId(loginId); //동우 사용자

            expirationDateService.setExpiration("E", 1, "SAFETY", EXP_MONTH, EXP_BODY, memberDTO.getUSER_IDX());
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }

    // admin -노동환경 만료일 - 업체별
    @RequestMapping("/admin/auditMgmt/expDateQuality")
    public String searchExpDateQuality(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        ExpirationDateDTO expirationDatDTO = expirationDateService.getExpiration("E", 1, "QUALITY");
        model.addAttribute("EXP_MONTH",expirationDatDTO.getEXP_MONTH());
        model.addAttribute("EXP_BODY",expirationDatDTO.getEXP_BODY());
        try {
            ObjectMapper mapper = new ObjectMapper();

            String COM_CODE = GetParam(request, "COM_CODE", "");
            String COM_NAME = GetParam(request, "COM_NAME", "");

            model.addAttribute("COM_CODE",COM_CODE);
            model.addAttribute("COM_NAME",COM_NAME);

            // 검색 조건에 맞는 결과를 반환
            List<HashMap> auditList = auditCommonService.getExpDateList(COM_CODE,COM_NAME,"QUALITY");
            String auditListStr = mapper.writeValueAsString(auditList);
            log.info("auditListStr::::::::::::::::::::::"+auditList);
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(auditListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("auditList",auditListStr);
        } catch (Exception e) {
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||" + e.getMessage());
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            } else {
                return  "redirect:/main";
            }
        }
        return "expDateQuality/main";
    }

    //품질관리 만료일 설정
    @PostMapping("/admin/auditMgmt/expDateQualitySave")
    public ResponseEntity<?> expDateQualitySave(HttpServletRequest request, HttpSession session) {
        try {
            int EXP_MONTH = Integer.parseInt(GetParam(request, "EXP_MONTH", "0"));
            String EXP_BODY = GetParam(request, "EXP_BODY", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loginId = user.getUsername();
            UserMgrDTO memberDTO = memberService.findByMemberId(loginId); //동우 사용자

            expirationDateService.setExpiration("E", 1, "QUALITY", EXP_MONTH, EXP_BODY, memberDTO.getUSER_IDX());
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }


    // admin -분쟁광물 만료일 - 업체별
    @RequestMapping("/admin/auditMgmt/expDateConflict")
    public String searchExpDateConflict(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header) {
        ExpirationDateDTO expirationDatDTO = expirationDateService.getExpiration("E", 1, "CONFLICT");
        //model.addAttribute("EXP_MONTH",expirationDatDTO.getEXP_MONTH());
        model.addAttribute("EXP_BODY",expirationDatDTO.getEXP_BODY());
        try {
            ObjectMapper mapper = new ObjectMapper();

            String COM_CODE = GetParam(request, "COM_CODE", "");
            String COM_NAME = GetParam(request, "COM_NAME", "");

            model.addAttribute("COM_CODE",COM_CODE);
            model.addAttribute("COM_NAME",COM_NAME);

            // 검색 조건에 맞는 결과를 반환
            List<HashMap> auditList = auditCommonService.getExpDateList(COM_CODE,COM_NAME,"CONFLICT");
            String auditListStr = mapper.writeValueAsString(auditList);
            log.info("auditListStr::::::::::::::::::::::"+auditList);
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(auditListStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("auditList",auditListStr);
        } catch (Exception e) {
            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||" + e.getMessage());
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            } else {
                return  "redirect:/main";
            }
        }
        return "expDateConflict/main";
    }

    //분쟁광물 만료일 설정
    @PostMapping("/admin/auditMgmt/expDateConflictSave")
    public ResponseEntity<?> expDateConflictSave(HttpServletRequest request, HttpSession session) {
        try {
            int EXP_MONTH = Integer.parseInt(GetParam(request, "EXP_MONTH", "0"));
            String EXP_BODY = GetParam(request, "EXP_BODY", "");

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loginId = user.getUsername();
            UserMgrDTO memberDTO = memberService.findByMemberId(loginId); //동우 사용자

            expirationDateService.setExpiration("E", 1, "CONFLICT", EXP_MONTH, EXP_BODY, memberDTO.getUSER_IDX());
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }

    private String GetParam(HttpServletRequest request, String pName, String pDefault) {
        String ParamValue = request.getParameter(pName);

        if (ParamValue == null || ParamValue.isEmpty()) {
            ParamValue = pDefault;
        };

        return ParamValue;
    }
}
