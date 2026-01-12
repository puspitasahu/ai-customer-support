package com.dailywork.aicustomersupport.event;

import com.dailywork.aicustomersupport.email.EmailNotificationService;
import com.dailywork.aicustomersupport.model.Ticket;

import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        log.info(" TicketCreationEventListener initialized and ready to listen for events");
    }

    @Override
    public void onApplicationEvent(TicketCreationEvent event) {
        log.info("════════════════════════════════════════");
        log.info(" TicketCreationEvent received!");
        log.info("   Thread: {}", Thread.currentThread().getName());

        Ticket ticket = event.getTicket();

        if (ticket == null) {
            log.error(" Ticket is NULL in event!");
            return;
        }

        log.info("   Ticket ID: {}", ticket.getId());
        log.info("   Reference: {}", ticket.getReferenceNumber());

        try {
            log.info("📧 Sending email notification...");
            emailNotificationService.sendTicketNotificationEmail(ticket);
            log.info(" Email notification sent successfully");
        } catch (Exception e) {  //  Catch ALL exceptions, not just MessagingException
            log.error(" Failed to send email notification", e);
            log.error("   Exception type: {}", e.getClass().getName());
            log.error("   Message: {}", e.getMessage());
        }

        log.info("════════════════════════════════════════");
    }
}
