package com.dailywork.aicustomersupport.service.chat;

import com.dailywork.aicustomersupport.dtos.ChatEntry;
import com.dailywork.aicustomersupport.dtos.ChatMessageDto;
import com.dailywork.aicustomersupport.helper.CustomerInfo;
import com.dailywork.aicustomersupport.helper.CustomerInfoHelper;
import com.dailywork.aicustomersupport.model.Ticket;
import com.dailywork.aicustomersupport.model.Customer;
import com.dailywork.aicustomersupport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService implements IConversationService {
    private final AISupportService aiSupportService;
    private final UserRepository userRepository;
    private Map<String, List<ChatEntry>> activeConversation = new ConcurrentHashMap<>();
    public String handleMessage(ChatMessageDto chatMessage) {
        String sessionId = chatMessage.getSessionId();
        String useMessage = chatMessage.getMessage()!=null ?chatMessage.getMessage().trim():"";
        log.info("The Session Id is {}",sessionId);
        log.info("The Messgae content is{}",useMessage);

        List<ChatEntry> history = activeConversation.computeIfAbsent(sessionId,k-> Collections.synchronizedList(new ArrayList<>()));
        history.add(new ChatEntry("user",useMessage));

        String aiResponseText;
        try{
            aiResponseText =aiSupportService.chatWithHistory(history).block();
        }catch(Exception e){
            aiResponseText ="Sorry,I am having trouble processing your request right now ";
        }
        if(aiResponseText ==null){
            return "";
        }

        if(aiResponseText.contains("TICKET_CREATION_READY")){
            try{
                String confirmationMessage = aiSupportService.generateUserConfirmationMessage().block();
                history.add(new ChatEntry("assistant",confirmationMessage));
                Ticket tempTicket = finalizeConversationAndCreateTicket(sessionId);
                log.info("Confirmed Ticket : {}",tempTicket);
                return confirmationMessage;
            }catch(Exception e){
                return "Error generating confirmation message";
            }

        }

        history.add(new ChatEntry("assistant",aiResponseText));
        return  aiResponseText;

    }

    private Ticket finalizeConversationAndCreateTicket(String sessionId){
        List<ChatEntry> history = activeConversation.get(sessionId);
        Customer user = getCustomerInformation(history);
        log.info("This is customer information :{}",user);
        if(user==null){
            String errorMessage = """
                    The email or Phone Number you provided doesn't exist in the database.please provide registered Emailaddress or Phone Number to continue
                    """;
            //Here we are going to send message to the user(WebSocket)
            if(history!=null){
                history.add(new ChatEntry("system",errorMessage));
            }
            return  null;
        }
        return null;
    }

    private Customer getCustomerInformation(List<ChatEntry> history){
        CustomerInfo customerInfo = CustomerInfoHelper.extractUserInformationChatHistory(history);
        log.info("CustomerInfo : {}",customerInfo);
        Customer customer= userRepository.findByEmailAddressAndPhoneNumber(customerInfo.emailAddress(),customerInfo.phoneNumber());
        return customer;

    }
}
