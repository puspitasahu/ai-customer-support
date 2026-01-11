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
    private  final EmailService emailService;
    private final CustomerService customerService;

    public void sendTicketNotificationEmail(Ticket ticket) {
        Customer customer =customerService.getCustomerByEmail(ticket.getConversation().getCustomer().getEmailAddress());
        String customerName = customer.getFullName();
        String customerEmail = customer.getEmailAddress();
        String senderName="Customer Support Service";
        String subject="Support Ticket Created";
        String body=getHtmlBody(ticket,customerName ,senderName);
        try{
            emailService.sendNotificationEmail(customerEmail,subject,senderName,body);
        }catch(Exception e){
            throw new RuntimeException(("Failed to send notification email"));
        }
    }

    private static String getHtmlBody(Ticket ticket,String userName ,String senderName){
        String ticketDetails = ticket.getConversation().getConversationSummary();
        String ticketTitle = ticket.getConversation().getConversationTitle();
        String referenceNumber = ticket.getReferenceNumber();

        return """
                <html>
                       <body>
                           <p>Dear %s ,</p>
                           <p>Thank you for contacting customer support, Your support ticket has been ccreated successfully</p>
                           <h3>Ticket Details : </h3>
                           <ul>
                               <li><Strong>Reference Number :</Strong> %s </li>
                               <li><Strong>Title :</Strong>  %s </li>
                               <li><Strong>Description :</Strong> %s </li>
                           </ul>
                           <p>If you have any questions, feel free to reply to this email or contact us at support@example.com</p>
                           <p>Best  regards, <br/> %s </p>
                       </body>
                </html>
               """.formatted(userName,referenceNumber,ticketTitle,ticketDetails,senderName);
    }

}
