package com.dongwoo.SQM.common.controller;

import com.dongwoo.SQM.common.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class EmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/sendEmail")
    public String sendEmail(@RequestParam("fromMail") String from, @RequestParam("to") String to, @RequestParam("subject") String subject, @RequestParam("text") String text) {
        log.info("sendEmail test:::::::::::::::::::::::::::::::전송 준비");
        from = "shhan@covision.co.kr";
        to = "shhan@covision.co.kr";
        subject = "Test Email";
        text = "Hello, this is a test email!";
        try {
            emailService.sendSimpleEmail(from, to, subject, text); // 발송자 이메일 동적으로 전달
            return "이메일 발송이 완료되었습니다.";
        } catch (Exception e) {
            return "이메일 발송에 실패했습니다: " + e.getMessage();
        }
    }
}
