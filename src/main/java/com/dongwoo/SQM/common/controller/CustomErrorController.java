package com.dongwoo.SQM.common.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
        private String VIEW_PATH = "/errors/";

        @GetMapping(value="/error")
        public String handleError (HttpServletRequest request) {
            Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

            if(status != null){
                int statusCode = Integer.valueOf(status.toString());

                if(statusCode == HttpStatus.NOT_FOUND.value()){
                    return VIEW_PATH + "404";
                }
                if(statusCode == HttpStatus.FORBIDDEN.value()){
                    return VIEW_PATH + "500";
                }
            }
            return "error";
        }

    @GetMapping("/example")
    public String examplePage(Model model) {
        model.addAttribute("errorMessage", "예외 발생 시 메시지");
        return "/error/example"; // Thymeleaf 템플릿 이름
    }

}
