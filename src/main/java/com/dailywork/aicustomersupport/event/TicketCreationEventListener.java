package com.dailywork.aicustomersupport.event;

import com.dailywork.aicustomersupport.email.EmailNotificationService;
import com.dailywork.aicustomersupport.model.Ticket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TicketCreationEventListener implements ApplicationListener<TicketCreationEvent> {
    private final EmailNotificationService emailNotificationService;
    @Override
    public void onApplicationEvent(TicketCreationEvent event) {
        Ticket ticket = event.getTicket();
        try{
            emailNotificationService.sendTicketNotificationEmail(ticket);
        }catch(MessagingException e){
            log.error("Fail to send ticket emailNotification to customer : {}",e.getFailedMessage());
        }
    }
}
