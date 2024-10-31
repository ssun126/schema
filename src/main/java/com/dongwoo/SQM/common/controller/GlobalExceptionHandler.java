package com.dongwoo.SQM.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc) {
        String errorMessage;
        errorMessage = "파일 크기가 너무 큽니다. 최대 크기를 확인하세요.";
        return errorMessage;
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception exc) {
        String errorMessage;
        errorMessage = "예기치 않은 오류가 발생했습니다: " + exc.getMessage();
        return errorMessage;
    }
}
