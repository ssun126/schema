package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.service.BaseCodeService;
import com.dongwoo.SQM.siteMgr.service.BaseConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/admin/siteMgr")
@RequiredArgsConstructor
public class BaseConfigController {

    private static final Logger log = LoggerFactory.getLogger(BaseConfigController.class);
    private final BaseConfigService BaseConfigService;
    private final BaseCodeService baseCodeService;

    @RequestMapping("/baseConfig")
    public String findAll(Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> header){
        try {
            ObjectMapper mapper = new ObjectMapper();

            String sGubn = request.getParameter("gubn");
            String sSearchKey = request.getParameter("searchkey");
            String sSearchVal = request.getParameter("searchval");

            if (sGubn == null || "null".equals(sGubn)) { sGubn = ""; };
            if (sSearchKey == null || "null".equals(sSearchKey)) { sSearchKey = ""; };
            if (sSearchKey.equals("") || sSearchVal == null || "null".equals(sSearchVal)) { sSearchVal = ""; };

            model.addAttribute("gubn",sGubn);
            model.addAttribute("searchkey",sSearchKey);
            model.addAttribute("searchval",sSearchVal);

            List<BaseConfigDTO> baseConfigDTOList = BaseConfigService.findSearch(sGubn,sSearchKey,sSearchVal);
            String baseConfigJsonStr = mapper.writeValueAsString(baseConfigDTOList);

            if (header.get("requesttype") != null && header.get("requesttype").toString().equals("ajax")) {
                try {
                    PrintWriter printer = response.getWriter();
                    printer.print(baseConfigJsonStr);
                    printer.close();
                } catch (Exception e) {
                }

                return "blank";
            }

            model.addAttribute("baseConfigList",baseConfigJsonStr);
        } catch (Exception e) {
            if (header.get("requesttype") != null && header.get("requesttype").toString().equals("ajax")) {
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

        return "/baseConfig/list";
    }

    @PostMapping("/baseConfigPopup")
    public String baseConfig_Popup(Model model, HttpServletRequest request, HttpServletResponse response){
        String mode = request.getParameter("mode");
        model.addAttribute("mode",mode);

        String IDX = "";
        String GUBN = "";
        String CONFIG_CODE = "";
        String CONFIG_VALUE = "";
        String CONFIG_STATUS = "";
        String CONFIG_SUMMARY = "";
        String CONFIG_OPTION = "";
        String CONFIG_OPTION2 = "";
        String CONFIG_OPTION3 = "";

        if (mode.equals("Edit")) {
            String idx = request.getParameter("IDX");
            BaseConfigDTO baseConfigDTO = BaseConfigService.getBaseConfig_Info(idx);
            if (baseConfigDTO != null) {
                IDX = idx;
                GUBN = baseConfigDTO.getGUBN();
                CONFIG_CODE = baseConfigDTO.getCONFIG_CODE();
                CONFIG_VALUE = baseConfigDTO.getCONFIG_VALUE();
                CONFIG_STATUS = baseConfigDTO.getCONFIG_STATUS();
                CONFIG_SUMMARY = baseConfigDTO.getCONFIG_SUMMARY();
                CONFIG_OPTION = baseConfigDTO.getCONFIG_OPTION();
                CONFIG_OPTION2 = baseConfigDTO.getCONFIG_OPTION2();
                CONFIG_OPTION3 = baseConfigDTO.getCONFIG_OPTION3();
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

        model.addAttribute("IDX",IDX);
        model.addAttribute("GUBN",GUBN);
        model.addAttribute("CONFIG_CODE",CONFIG_CODE);
        model.addAttribute("CONFIG_VALUE",CONFIG_VALUE);
        model.addAttribute("CONFIG_STATUS",CONFIG_STATUS);
        model.addAttribute("CONFIG_SUMMARY",CONFIG_SUMMARY);
        model.addAttribute("CONFIG_OPTION",CONFIG_OPTION);
        model.addAttribute("CONFIG_OPTION2",CONFIG_OPTION2);
        model.addAttribute("CONFIG_OPTION3",CONFIG_OPTION3);

        return "/baseConfig/BaseConfigPopUp";
    }

    @PostMapping("/baseConfigPopupSave")
    public ResponseEntity<?> baseConfig_PopupSave(HttpServletRequest request, HttpSession session) {
        try {
            String mode = GetParam(request, "mode", "");
            int IDX = Integer.parseInt(GetParam(request, "IDX", "0"));
            String GUBN = GetParam(request, "GUBN", "");
            String CONFIG_CODE = GetParam(request, "CONFIG_CODE", "");
            String CONFIG_VALUE = GetParam(request, "CONFIG_VALUE", "");
            String CONFIG_STATUS = GetParam(request, "CONFIG_STATUS", "");
            String CONFIG_OPTION = GetParam(request, "CONFIG_OPTION", "");
            String CONFIG_OPTION2 = GetParam(request, "CONFIG_OPTION2", "");
            String CONFIG_OPTION3 = GetParam(request, "CONFIG_OPTION3", "");
            String CONFIG_SUMMARY = GetParam(request, "CONFIG_SUMMARY", "");

            if ((mode.equals("Edit") || mode.equals("Del")) && IDX == 0) {
                return ResponseEntity.ok("|||[ERROR]|||코드 정보가 없습니다.");
            }
            if ((mode.equals("New") || mode.equals("Edit")) && CONFIG_CODE.isEmpty()) {
                return ResponseEntity.ok("|||[ERROR]|||설정키 정보가 없습니다.");
            }
            if ((mode.equals("New") || mode.equals("Edit")) && CONFIG_VALUE.isEmpty()) {
                return ResponseEntity.ok("|||[ERROR]|||설정값 정보가 없습니다.");
            }

            BaseConfigDTO baseConfigDTO = new BaseConfigDTO();
            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String comCode = user.getCOM_CODE();
            int loginIdx = user.getUSER_IDX();
            baseConfigDTO.setUSER_IDX(loginIdx);

            if (mode.equals("New")) {
                List<BaseConfigDTO> baseConfigDTOList = BaseConfigService.findSearch("","key",CONFIG_CODE);

                if (!baseConfigDTOList.isEmpty()) {
                    return ResponseEntity.ok("|||[ERROR]|||이미 등록되어 있는 코드 입니다.");
                }

                BaseConfigService.save(baseConfigDTO);
            } else if (mode.equals("Edit")) {
                baseConfigDTO.setIDX(IDX);
                baseConfigDTO.setGUBN(GUBN);
                baseConfigDTO.setCONFIG_CODE(CONFIG_CODE.trim());
                baseConfigDTO.setCONFIG_VALUE(CONFIG_VALUE);
                baseConfigDTO.setCONFIG_STATUS(CONFIG_STATUS);
                baseConfigDTO.setCONFIG_OPTION(CONFIG_OPTION);
                baseConfigDTO.setCONFIG_OPTION2(CONFIG_OPTION2);
                baseConfigDTO.setCONFIG_OPTION3(CONFIG_OPTION3);
                baseConfigDTO.setCONFIG_OPTION3(CONFIG_OPTION3);
                baseConfigDTO.setCONFIG_SUMMARY(CONFIG_SUMMARY);

                BaseConfigService.update(baseConfigDTO);
            } else if (mode.equals("Del")) {
                baseConfigDTO.setIDX(IDX);
                BaseConfigService.delete(IDX);
            }
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }

    private String GetParam(HttpServletRequest request, String pName, String pDefault) {
        String ParamValue = request.getParameter(pName);

        if (ParamValue == null || "null".equals(ParamValue) || ParamValue.isEmpty()) {
            ParamValue = pDefault;
        };

        return ParamValue;
    }
}
