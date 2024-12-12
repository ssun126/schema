package com.dongwoo.SQM.common.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalCommonHandler {

    //ExceptionHandler
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc) {
        String errorMessage;
        errorMessage = "파일 크기가 너무 큽니다. 최대 크기를 확인하세요.";
        return errorMessage;
    }
    //ExceptionHandler
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception exc) {
        String errorMessage;
        errorMessage = "예기치 않은 오류가 발생했습니다: " + exc.getMessage();
        return errorMessage;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public String handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String errorMessage;
        errorMessage = "타입에 맞지 않습니다. 확인하세요.";
        return errorMessage;
    }



    //현재URL정보 가져오기
    @ModelAttribute("currentUrl")
    public String currentUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return request.getRequestURI() + (queryString != null ? "?" + queryString : "");
    }
    
}
