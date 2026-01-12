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
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendNotificationEmail(String to, String subject, String senderName, String mailContent)
            throws MessagingException, UnsupportedEncodingException {

        log.info("════════════════════════════════════════");
        log.info(" EmailService.sendNotificationEmail");
        log.info("   To: {}", to);
        log.info("   Subject: {}", subject);
        log.info("   From: {}", EmailProperties.DEFAULT_USERNAME);
        log.info("   Sender Name: {}", senderName);

        try {
            log.info(" Creating MimeMessage...");
            MimeMessage message = mailSender.createMimeMessage();

            log.info(" Creating MimeMessageHelper...");
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");  // Fix: UTF-8 not UTF_8

            log.info(" Setting email properties...");
            messageHelper.setFrom(EmailProperties.DEFAULT_USERNAME, senderName);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(mailContent, true);

            log.info(" Sending email via mailSender.send()...");
            mailSender.send(message);

            log.info("Email sent successfully!");
            log.info("════════════════════════════════════════");

        } catch (MessagingException e) {
            log.error("════════════════════════════════════════");
            log.error(" MessagingException in EmailService");
            log.error("   Message: {}", e.getMessage());
            log.error("   Stack trace:", e);
            log.error("════════════════════════════════════════");
            throw e;

        } catch (UnsupportedEncodingException e) {
            log.error("════════════════════════════════════════");
            log.error(" UnsupportedEncodingException in EmailService");
            log.error("   Message: {}", e.getMessage());
            log.error("   Stack trace:", e);
            log.error("════════════════════════════════════════");
            throw e;

        } catch (Exception e) {
            log.error("════════════════════════════════════════");
            log.error("Unexpected Exception in EmailService");
            log.error("   Type: {}", e.getClass().getName());
            log.error("   Message: {}", e.getMessage());
            log.error("   Stack trace:", e);
            log.error("════════════════════════════════════════");
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
