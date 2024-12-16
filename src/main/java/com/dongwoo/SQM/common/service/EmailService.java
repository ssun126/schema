package com.dongwoo.SQM.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail; //설정에 발송 메일 정보 가져오기

    // 간단한 텍스트 이메일 발송
    public void sendSimpleEmail(String from, String toMail, String subject, String text) throws MessagingException, UnsupportedEncodingException {
        // MimeMessage 객체를 생성
        MimeMessage mimeMessage = emailSender.createMimeMessage();

        // MimeMessageHelper로 이메일 내용 설정
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
        message.setFrom(from, "메일발송 담당자");  // 발신자 이메일, 발송자 명
        //message.setFrom(fromEmail);  // 발신자 이메일
        message.setTo(toMail);                       // 수신자 이메일
        message.setSubject(subject);             // 이메일 제목
        message.setText(text);                   // 이메일 본문
        try {
            log.info("sendEmail test:::::::::::::::::::::::::::::::전송>>>>>>"+message);
            emailSender.send(mimeMessage);
        } catch (MailSendException e) {
            // 예외 처리 로직
            System.out.println("Mail send failed: " + e.getMessage());
        } catch (Exception e) {
            // 일반적인 예외 처리
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    // HTML 형식의 이메일 발송
    public void sendHtmlEmail(String to, String subject, String htmlText) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlText, true);  // true로 설정하면 HTML 형식의 이메일 발송

        try {
            emailSender.send(mimeMessage);
        } catch (MailSendException e) {
            // 예외 처리 로직
            System.out.println("Mail send failed: " + e.getMessage());
        } catch (Exception e) {
            // 일반적인 예외 처리
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
