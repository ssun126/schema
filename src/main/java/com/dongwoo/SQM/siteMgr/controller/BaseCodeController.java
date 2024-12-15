package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.siteMgr.service.BaseCodeService;
import com.dongwoo.SQM.system.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/admin/siteMgr")
@RequiredArgsConstructor
public class BaseCodeController {

    private static final Logger log = LoggerFactory.getLogger(BaseCodeController.class);
    private final BaseCodeService BaseCodeService;
    private final MemberService memberService;

    @RequestMapping("/baseCode")
    public String findAll(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header){
        try {
            List<BaseCodeDTO> baseCodeDTOGroupList = BaseCodeService.findByCodeGroupAll();
            model.addAttribute("baseGroupList",baseCodeDTOGroupList);

            ObjectMapper mapper = new ObjectMapper();

            String sGubn = GetParam(request, "gubn", "");
            String sCodeGroup = GetParam(request, "codeGroup", "");
            String sSearchKey = GetParam(request, "searchkey", "");
            String sSearchVal = GetParam(request, "searchval", "");

            model.addAttribute("gubn",sGubn);
            model.addAttribute("codeGroup",sCodeGroup);
            model.addAttribute("searchkey",sSearchKey);
            model.addAttribute("searchval",sSearchVal);

            List<BaseCodeDTO> baseCodeDTOList = BaseCodeService.findSearch(sGubn,sCodeGroup,sSearchKey,sSearchVal);
            String baseCodeJsonStr = mapper.writeValueAsString(baseCodeDTOList);

            if (header.get("requesttype") != null && header.get("requesttype").equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(baseCodeJsonStr);
                    printer.close();
                } catch (Exception ignored) {
                }

                return "blank";
            }

            model.addAttribute("baseCodeList",baseCodeJsonStr);
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

        return "/baseCode/list";
    }

    @PostMapping("/baseCodePopup")
    public String baseCode_Popup(Model model, HttpServletRequest request, HttpServletResponse response){
        List<BaseCodeDTO> baseCodeDTOGroupList = BaseCodeService.findByCodeGroupAll();
        model.addAttribute("baseGroupList",baseCodeDTOGroupList);

        String mode = request.getParameter("mode");
        model.addAttribute("mode",mode);

        String BASE_CODE_IDX = "";
        String GUBN = "";
        String BASE_CODE = "";
        String BASE_NAME = "";
        String BASE_OPTION = "";
        String BASE_OPTION2 = "";
        String BASE_OPTION3 = "";
        String BASE_VALUE = "";
        String BASE_SUMMARY = "";
        String BASE_STATUS = "";
        int BASE_SORT = 0;
        String GROUP_CODE = "";

        if (mode.equals("Edit")) {
            BASE_CODE_IDX = GetParam(request,"BASE_CODE_IDX", "");
            BaseCodeDTO baseCodeDTO = BaseCodeService.getbaseCodeInfo(BASE_CODE_IDX);
            if (baseCodeDTO != null) {
                GUBN = baseCodeDTO.getGUBN();
                BASE_CODE = baseCodeDTO.getBASE_CODE();
                BASE_NAME = baseCodeDTO.getBASE_NAME();
                BASE_OPTION = baseCodeDTO.getBASE_OPTION();
                BASE_OPTION2 = baseCodeDTO.getBASE_OPTION2();
                BASE_OPTION3 = baseCodeDTO.getBASE_OPTION3();
                BASE_VALUE = baseCodeDTO.getBASE_VALUE();
                BASE_SUMMARY = baseCodeDTO.getBASE_SUMMARY();
                BASE_STATUS = baseCodeDTO.getBASE_STATUS();
                BASE_SORT = baseCodeDTO.getBASE_SORT();
                GROUP_CODE = baseCodeDTO.getGROUP_CODE();
            } else {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print("|||[ERROR]|||기초설정 정보가 존재하지 않습니다.");
                    printer.close();
                } catch (Exception e2) {
                }

                return "blank";
            }
        }

        model.addAttribute("BASE_CODE_IDX",BASE_CODE_IDX);
        model.addAttribute("GUBN",GUBN);
        model.addAttribute("BASE_CODE",BASE_CODE);
        model.addAttribute("BASE_NAME",BASE_NAME);
        model.addAttribute("BASE_OPTION",BASE_OPTION);
        model.addAttribute("BASE_OPTION2",BASE_OPTION2);
        model.addAttribute("BASE_OPTION3",BASE_OPTION3);
        model.addAttribute("BASE_VALUE",BASE_VALUE);
        model.addAttribute("BASE_SUMMARY",BASE_SUMMARY);
        model.addAttribute("BASE_STATUS",BASE_STATUS);
        model.addAttribute("BASE_SORT",BASE_SORT);
        model.addAttribute("GROUP_CODE",GROUP_CODE);

        return "/baseCode/BaseCodePopUp";
    }

    @PostMapping("/baseCodePopupSave")
    public ResponseEntity<?> baseCode_PopupSave(HttpServletRequest request, HttpSession session) {
        try {
            String mode = GetParam(request, "mode", "");
            int BASE_CODE_IDX = Integer.parseInt(GetParam(request, "BASE_CODE_IDX", "0"));
            String GUBN = GetParam(request, "GUBN", "");
            String BASE_CODE = GetParam(request, "BASE_CODE", "");
            String BASE_NAME = GetParam(request, "BASE_NAME", "");
            String BASE_OPTION = GetParam(request, "BASE_OPTION", "");
            String BASE_OPTION2 = GetParam(request, "BASE_OPTION2", "");
            String BASE_OPTION3 = GetParam(request, "BASE_OPTION3", "");
            String BASE_VALUE = GetParam(request, "BASE_VALUE", "");
            String BASE_SUMMARY = GetParam(request, "BASE_SUMMARY", "");
            String BASE_STATUS = GetParam(request, "BASE_STATUS", "");
            int BASE_SORT = Integer.parseInt(GetParam(request, "BASE_SORT", "0"));
            String GROUP_CODE = GetParam(request, "GROUP_CODE", "");

            if ((mode.equals("Edit") || mode.equals("Del")) && BASE_CODE_IDX == 0) {
                return ResponseEntity.ok("|||[ERROR]|||코드 정보가 없습니다.");
            }
            if ((mode.equals("New") || mode.equals("Edit")) && BASE_CODE.isEmpty()) {
                return ResponseEntity.ok("|||[ERROR]|||설정키 정보가 없습니다.");
            }

            BaseCodeDTO baseCodeDTO = new BaseCodeDTO();
            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loginId = user.getUsername();
            UserMgrDTO memberDTO = memberService.findByMemberId(loginId); //동우 사용자

            int loginIdx = memberDTO.getUSER_IDX();
            baseCodeDTO.setREG_DW_USER_IDX(loginIdx);
            baseCodeDTO.setUP_DW_USER_IDX(loginIdx);

            if ((mode.equals("New") || mode.equals("Edit"))) {
                if (GROUP_CODE.isEmpty()) {
                    GROUP_CODE = BASE_CODE.trim();
                }

                baseCodeDTO.setGUBN(GUBN);
                baseCodeDTO.setBASE_CODE(BASE_CODE.trim());
                baseCodeDTO.setBASE_NAME(BASE_NAME);
                baseCodeDTO.setBASE_OPTION(BASE_OPTION);
                baseCodeDTO.setBASE_OPTION2(BASE_OPTION2);
                baseCodeDTO.setBASE_OPTION3(BASE_OPTION3);
                baseCodeDTO.setBASE_VALUE(BASE_VALUE);
                baseCodeDTO.setBASE_SUMMARY(BASE_SUMMARY);
                baseCodeDTO.setBASE_STATUS(BASE_STATUS);
                baseCodeDTO.setBASE_SORT(BASE_SORT);
                baseCodeDTO.setGROUP_CODE(GROUP_CODE);
            }

            if ((mode.equals("Edit") || mode.equals("Del"))) {
                baseCodeDTO.setBASE_CODE_IDX(BASE_CODE_IDX);
            }

            if (mode.equals("New")) {
                BaseCodeDTO baseCodeDTOInfo = BaseCodeService.getbaseCodeInfoCode(BASE_CODE.trim());

                if (baseCodeDTOInfo != null) {
                    return ResponseEntity.ok("|||[ERROR]|||이미 등록되어 있는 코드 입니다.");
                }

                BaseCodeService.save(baseCodeDTO);
            } else if (mode.equals("Edit")) {
                BaseCodeService.update(baseCodeDTO);
            } else if (mode.equals("Del")) {
                BaseCodeDTO baseCodeDTOInfo = BaseCodeService.getbaseCodeInfo(BASE_CODE_IDX);

                if (baseCodeDTOInfo.getBASE_CODE().equals(baseCodeDTOInfo.getGROUP_CODE())) {
                    List<BaseCodeDTO> baseCodeDTOUse = BaseCodeService.findByCodeGroupUse(baseCodeDTOInfo.getBASE_CODE());
                    if (!baseCodeDTOUse.isEmpty()) {
                        return ResponseEntity.ok("|||[ERROR]|||사용중인 그룹코드 입니다.");
                    }
                }

                BaseCodeService.delete(BASE_CODE_IDX);
            }
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
