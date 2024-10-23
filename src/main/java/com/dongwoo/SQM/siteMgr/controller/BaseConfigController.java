package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.service.BaseConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BaseConfigController {
    private final BaseConfigService BaseConfigService;

    @GetMapping("/siteMgr/baseConfig")
    public String baseConfig(){ return "/baseConfig/list"; }

}
