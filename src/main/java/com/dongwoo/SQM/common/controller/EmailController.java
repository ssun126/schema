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
    public String sendEmail(@RequestParam("to") String to, @RequestParam("subject") String subject, @RequestParam("text") String text) {
        log.info("sendEmail test:::::::::::::::::::::::::::::::전송 준비");
        to = "shhan@covision.co.kr";
        subject = "Test Email";
        text = "Hello, this is a test email!";
        emailService.sendSimpleEmail(to, subject, text);
        return "Email sent successfully!";
    }
}
