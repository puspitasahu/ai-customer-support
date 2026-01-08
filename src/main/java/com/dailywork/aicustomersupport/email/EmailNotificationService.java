package com.dailywork.aicustomersupport.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

import static com.dailywork.aicustomersupport.email.EmailProperties.DEFAULT_USERNAME;

@RequiredArgsConstructor
@Slf4j
@Component
public class EmailNotificationService {
    private final JavaMailSender mailSender;

    public void sendNotificationEmail(String to,String subject,String senderName,String mailContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message,true,"UTF_8");
        messageHelper.setFrom(DEFAULT_USERNAME,senderName);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent,true);
        mailSender.send(message);
        log.info("Email message sent successfully : {}",message);


    }
}
