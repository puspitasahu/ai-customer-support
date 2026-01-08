package com.dailywork.aicustomersupport.email;

import com.dailywork.aicustomersupport.model.Customer;
import com.dailywork.aicustomersupport.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailNotificationService {
    private  final EmailService emailService;

    public void sendTicketNotificationEmail(Ticket ticket) {
        Customer customer =null;
        String customerName = null;
        String customerEmail = null;
        String senderName="Customer Support Service";
        String subject="Support Ticket Created";
        String body="";
        try{
            emailService.sendNotificationEmail(customerEmail,subject,senderName,body);
        }catch(Exception e){
            throw new RuntimeException(("Failed to send notification email"));
        }



    }

}
