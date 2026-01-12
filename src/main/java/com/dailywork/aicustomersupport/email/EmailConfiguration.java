package com.dailywork.aicustomersupport.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static com.dailywork.aicustomersupport.email.EmailProperties.*;

@Configuration
public class EmailConfiguration {
    @Bean
    public JavaMailSender createMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(DEFAULT_HOST);
        mailSender.setPort(DEFAULT_PORT);
        mailSender.setUsername(DEFAULT_SENDER);
        mailSender.setPassword(DEFAULT_PASSWORD);
        Properties props =mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth",DEFAULT_AUTH);
        props.put("mail.smtp.starttls.enable",DEFAULT_STARTTLS);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return mailSender;
    }
}
