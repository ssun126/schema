package com.dongwoo.SQM.qualityCtrl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@Slf4j
@Controller
@RequiredArgsConstructor
public class changeMgmtController {
    @GetMapping("/admin/qualityCtrl/changeMgmt")
    public String isoAuthMain(Model model) {
        return "changeMgmt/main";
    }

    //@RequestMapping(value = "/admin/qualityCtrl/changeMgmt/Popup",method = RequestMethod.GET)
    @GetMapping("/admin/qualityCtrl/changeMgmt/Popup")
    public ModelAndView baseConfig_Popup(Locale locale, Model model){
        String returnUrl = "/changeMgmt/detail";
        ModelAndView mav = new ModelAndView(returnUrl);
        return mav;
    }
}

