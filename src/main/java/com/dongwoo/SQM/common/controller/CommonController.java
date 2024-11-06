package com.dongwoo.SQM.common.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class CommonController {

    @GetMapping("/common/modal")
    public String examplePage(Model model) {
        model.addAttribute("msg", "성공");
        return "/common/pages/modal"; // Thymeleaf 템플릿 이름
    }

}
