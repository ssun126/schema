package com.dongwoo.SQM.config.security;

import lombok.Getter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;


public class FormWebAuthenticationDetails extends WebAuthenticationDetails {
    @Getter
    private String comUserIdx;

    public FormWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        comUserIdx =  request.getParameter("comUserIdx");
    }

}
