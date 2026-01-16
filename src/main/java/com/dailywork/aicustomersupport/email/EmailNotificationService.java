package com.dailywork.aicustomersupport.email;

import com.dailywork.aicustomersupport.model.Customer;
import com.dailywork.aicustomersupport.model.Ticket;
import com.dailywork.aicustomersupport.service.customer.CustomerService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailNotificationService {
    private final EmailService emailService;
    private final CustomerService customerService;

    public void sendTicketNotificationEmail(Ticket ticket) {
        log.info("════════════════════════════════════════");
        log.info(" Preparing to send email for ticket: {}", ticket.getId());

        try {
            Customer customer = customerService.getCustomerByEmail(
                    ticket.getConversation().getCustomer().getEmailAddress()
            );

            if (customer == null) {
                log.error(" Customer not found!");
                throw new RuntimeException("Customer not found");
            }

            String customerName = customer.getFullName();
            String customerEmail = customer.getEmailAddress();
            String customerPhone = customer.getPhoneNumber();

            String ticketDetails = ticket.getConversation().getConversationSummary();
            String ticketTitle = ticket.getConversation().getConversationTitle();
            String referenceNumber = ticket.getReferenceNumber();

            log.info("   Customer: {}", customerName);
            log.info("   Email: {}", customerEmail);

            String senderName = "Customer Support Service";
            String subject = "Support Ticket Created";
            String htmlBody = null;

           try{
               htmlBody = loadEmailTemplate(customerName,customerEmail,customerPhone,ticketDetails,ticketTitle,referenceNumber);
           }catch(Exception e){
            log.error("Error loading email template: ",e);
           }
           try{
               log.info(" Calling emailService.sendNotificationEmail()...");
               emailService.sendNotificationEmail(customerEmail, subject, senderName, htmlBody);
               log.info(" Email sent successfully!");
           }catch(MessagingException | UnsupportedEncodingException e){
               log.error("Error sending email notification:",e);
           }


        } catch (Exception e) {
            log.error("════════════════════════════════════════");
            log.error(" Failed to send email");
            log.error("   Exception type: {}", e.getClass().getName());
            log.error("   Exception message: {}", e.getMessage());
            log.error("   Stack trace:", e);
            log.error("════════════════════════════════════════");

            // Include the actual error message!
            throw new RuntimeException("Failed to send notification email: " + e.getMessage(), e);
        }

        log.info("════════════════════════════════════════");
    }

    private String loadEmailTemplate(String customerName,String customerEmail,String customerPhone,String ticketDetails,String ticketTitle,String referenceNumber) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/ticket-notification-template.html");
        String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        template = template.replace("{{customerName}}", customerEmail);
        template = template.replace("{{customerEmail}}",customerEmail);
        template = template.replace("{{customerPhone}}",customerPhone);
        template = template.replace("{{ticketDetails}}",ticketDetails);
        template = template.replace("{{ticketTitle}}",ticketTitle);
        template = template.replace("{{ticketReferenceNumber}}",referenceNumber);
        return template;
    }

    /*private static String getHtmlBody(Ticket ticket, String userName, String senderName) {
        String ticketDetails = ticket.getConversation().getConversationSummary();
        String ticketTitle = ticket.getConversation().getConversationTitle();
        String referenceNumber = ticket.getReferenceNumber();

        return """
                <html>
                    <body>
                        <p>Dear %s,</p>
                        <p>Thank you for contacting customer support. Your support ticket has been created successfully.</p>
                        <h3>Ticket Details:</h3>
                        <ul>
                            <li><strong>Reference Number:</strong> %s</li>
                            <li><strong>Title:</strong> %s</li>
                            <li><strong>Description:</strong> %s</li>
                        </ul>
                        <p>If you have any questions, feel free to reply to this email or contact us at support@example.com</p>
                        <p>Best regards,<br/>%s</p>
                    </body>
                </html>
                """.formatted(userName, referenceNumber, ticketTitle, ticketDetails, senderName);
    }*/
}