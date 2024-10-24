package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.service.BaseConfigService;
import com.fasterxml.jackson.annotation.JsonTypeId;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BaseConfigController {

    private static final Logger log = LoggerFactory.getLogger(BaseConfigController.class);
    private final BaseConfigService BaseConfigService;

//    @GetMapping("/baseConfig")
//    public String baseConfig(){
//        log.info("================test11111");
//        return "/baseConfig/getlist";
//    }

    @GetMapping("/siteMgr/baseConfig")
    public String findAll(Model model){
        List<BaseConfigDTO> baseConfigDTOList = BaseConfigService.findAll();
        model.addAttribute("baseConfigList",baseConfigDTOList);
        log.info("================test22222222");
        //return "/siteMgr/baseConfig";
        return "/baseConfig/list";
    }

    @GetMapping("/baseconfig/search")
    public String findSearch(Model model, HttpServletRequest request){
        String sGubun = request.getParameter("gubun");
        String sSearchKey = request.getParameter("searchkey");
        String sSearchVal = request.getParameter("searchval");

        List<BaseConfigDTO> baseConfigDTOList = BaseConfigService.findSearch(sGubun,sSearchKey,sSearchVal);
        model.addAttribute("baseConfigList",baseConfigDTOList);
        log.info("================test22222222");
        //return "/siteMgr/baseConfig";
        return "/baseConfig/list";
    }

    @GetMapping("/BaseConfig/Popup")
    public String baseConfig_Popup(){
        return "/baseConfig/BaseConfigPopUp";
    }

}
