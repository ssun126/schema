package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.service.BaseCodeService;
import com.dongwoo.SQM.siteMgr.service.BaseConfigService;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class BaseConfigController {

    private static final Logger log = LoggerFactory.getLogger(BaseConfigController.class);
    private final BaseConfigService BaseConfigService;
    private final BaseCodeService baseCodeService;

//    @GetMapping("/baseConfig")
//    public String baseConfig(){
//        log.info("================test11111");
//        return "/baseConfig/getlist";
//    }

    @GetMapping("/siteMgr/baseConfig")
    public String findAll(Model model){
        List<BaseConfigDTO> baseConfigDTOList = BaseConfigService.findAll();
        model.addAttribute("baseConfigList",baseConfigDTOList);
        List<BaseCodeDTO> baseGubunList = baseCodeService.getbaseGubunList();
        model.addAttribute("baseGubunList",baseGubunList);
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
        List<BaseCodeDTO> baseGubunList = baseCodeService.getbaseGubunList();
        model.addAttribute("baseGubunList",baseGubunList);
        log.info("================test22222222");
        //return "/siteMgr/baseConfig";
        return "/baseConfig/list";
    }

    @RequestMapping(value = "/BaseConfig/Popup",method = RequestMethod.GET)
    public ModelAndView baseConfig_Popup(Locale locale, Model model){
        String returnUrl = "/baseConfig/BaseConfigPopUp";
        ModelAndView mav = new ModelAndView(returnUrl);
        return mav;
    }


    @PostMapping("/baseConfig/baseConfig_Info")
    public @ResponseBody Map<String, String> getBaseConfig_Info(@RequestParam("status") String idx) {
        System.out.println("status = " + idx);
        BaseConfigDTO baseConfigDTO = BaseConfigService.getBaseConfig_Info(idx);
        log.info("================test22222222aa");

        HashMap<String,String> response = new HashMap<>();

        if(baseConfigDTO != null) {
            response.put("status", "ok");
            response.put("IDX", String.valueOf(baseConfigDTO.getIDX()));
            response.put("GUBN", String.valueOf(baseConfigDTO.getGUBN()));
            response.put("CONFIGCODE", String.valueOf(baseConfigDTO.getCONFIGCODE()));
            response.put("CONFIGVALUE", String.valueOf(baseConfigDTO.getCONFIGVALUE()));
            response.put("CONFIGSUMMARY", String.valueOf(baseConfigDTO.getCONFIGSUMMARY()));
            response.put("CONFIGSTATUS", String.valueOf(baseConfigDTO.getCONFIGSTATUS()));
        }
        return response;
    }

    @GetMapping("/baseconfig/action")
    public String save(@ModelAttribute BaseConfigDTO baseConfigDTO,HttpSession session) {
        log.info("test111111");
        log.info(baseConfigDTO.getUSERID());
        String sUserID = (String) session.getAttribute("loginId");
        if(sUserID==null) sUserID="1";
        log.info(sUserID);
        baseConfigDTO.setUSERID(sUserID);

        //String sFlag = httpServletRequest.getParameter("baseConfigFlag");
        String sFlag = baseConfigDTO.getINFOFLAG();
        log.info(sFlag);
        // add : 추가, Mod : 수정, Del :  삭제
        if(sFlag.equals("Add")) {
            log.info("test= 추가");
            BaseConfigService.save(baseConfigDTO);
            log.info("test= 추가끝");
        }else if(sFlag.equals("Mod")){
            BaseConfigService.update(baseConfigDTO);
        }else{
            int sidx = baseConfigDTO.getIDX();
            BaseConfigService.delete(sidx);
        }


        return  "redirect:/siteMgr/baseConfig";
    }


}
