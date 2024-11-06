package com.dongwoo.SQM.system.controller;

import com.dongwoo.SQM.system.dto.MultiLanguageDTO;
import com.dongwoo.SQM.system.service.MultiLanguageService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Controller
@Slf4j
public class MultiLanguageController {
    @Autowired
    private MultiLanguageService multiLanguageService;

    @GetMapping("/multiLanguage/list")
    public String findAll(@ModelAttribute MultiLanguageDTO multiLanguageDTO, HttpSession session, Model model) throws Exception{

        boolean isSuccess = false;
        List<MultiLanguageDTO> multiLanguageList = multiLanguageService.findAll();

        model.addAttribute("list", multiLanguageList);
        model.addAttribute("isSuccess", isSuccess);
        return "/multiLanguage/list";
    }


    @PostMapping("/multiLanguage/setLocalStorage")
    public @ResponseBody List<HashMap> setLocalStorage(HttpSession session, Model model)  throws Exception{

        List<HashMap> multiLanguageList =  multiLanguageService.getMultiLanguageList_HashMap();
        //log.info("multiLanguageList = " + multiLanguageList);

        return multiLanguageList;
    }
}
