package com.dailywork.aicustomersupport.email;

import com.dailywork.aicustomersupport.model.Customer;
import com.dailywork.aicustomersupport.model.Ticket;
import com.dailywork.aicustomersupport.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

            log.info("   Customer: {}", customerName);
            log.info("   Email: {}", customerEmail);

            String senderName = "Customer Support Service";
            String subject = "Support Ticket Created";
            String body = getHtmlBody(ticket, customerName, senderName);

            log.info(" Calling emailService.sendNotificationEmail()...");
            emailService.sendNotificationEmail(customerEmail, subject, senderName, body);
            log.info(" Email sent successfully!");

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

    private static String getHtmlBody(Ticket ticket, String userName, String senderName) {
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
    }
}