package com.dailywork.aicustomersupport.service.chat;

import com.dailywork.aicustomersupport.dtos.ChatEntry;
import com.dailywork.aicustomersupport.dtos.ChatMessageDto;
import com.dailywork.aicustomersupport.helper.CustomerInfo;
import com.dailywork.aicustomersupport.helper.CustomerInfoHelper;
import com.dailywork.aicustomersupport.model.Conversation;
import com.dailywork.aicustomersupport.model.Ticket;
import com.dailywork.aicustomersupport.model.Customer;
import com.dailywork.aicustomersupport.repository.ConversationRepository;
import com.dailywork.aicustomersupport.repository.UserRepository;
import com.dailywork.aicustomersupport.service.Ticket.ITicketService;
import com.dailywork.aicustomersupport.websocket.WebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService implements IConversationService {
    private final AISupportService aiSupportService;
    private final UserRepository userRepository;
    private final WebSocketMessageSender webSocketMessageSender;
    private final ConversationRepository conversationRepository;
    private final ITicketService iTicketService;
    private Map<String, List<ChatEntry>> activeConversation = new ConcurrentHashMap<>();
    public String handleMessage(ChatMessageDto chatMessage) {
        String sessionId = chatMessage.getSessionId();
        String message = chatMessage.getMessage()!=null ?chatMessage.getMessage().trim():"";
        log.info("The Session Id is {}",sessionId);
        log.info("The Messgae content is{}", message);

        List<ChatEntry> history = activeConversation.computeIfAbsent(sessionId,k-> Collections.synchronizedList(new ArrayList<>()));
        history.add(new ChatEntry("user", message));

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

                CompletableFuture.runAsync(()->{
                    try{
                        Ticket  tempTicket = finalizeConversationAndCreateTicket(sessionId);
                        if(tempTicket !=null){
                            history.add(new ChatEntry("system","The email has been sent to user"));
                        }
                        //Ask AI to generate report and send email to customer
                        String feedbackMessage = aiSupportService.generateEmailNotificationMessage().block();
                        if(feedbackMessage !=null){
                            List<ChatEntry> currentHistory = activeConversation.get(sessionId);
                            if(currentHistory == null){
                                currentHistory.add(new ChatEntry("assistant",feedbackMessage));
                            }
                            webSocketMessageSender.sendMessageToUser(sessionId,feedbackMessage);
                        }
                    }catch(Exception e){
                        //log any error
                    }

                });

                //log.info("Confirmed Ticket : {}",tempTicket);
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
        Customer customer = getCustomerInformation(history);
        log.info("This is customer information :{}",customer);
        if(customer==null){
            String errorMessage = """
                    The email or Phone Number you provided doesn't exist in the database.please provide registered Emailaddress or Phone Number to continue
                    """;
            //Here we are going to send message to the user(WebSocket)
            webSocketMessageSender.sendMessageToUser(sessionId,errorMessage);
            if(history!=null){
                history.add(new ChatEntry("system",errorMessage));
            }
            //set up flag and wait for customer information correction
            return  null;
        }

        Conversation  conversation =  getConversation( customer);

        try{

            List<ChatEntry> userConversation = history.stream().filter(entry->"user".equalsIgnoreCase(entry.getRole()))
                    .toList();
            String conversationSummary = aiSupportService.summarizeCustomerConversationMessage(userConversation.toString()).block();
            String conversationTitle = aiSupportService.conversationTitle(conversationSummary).block();

            conversation.setConversationTitle(conversationTitle!=null ? conversationTitle.trim() : "UnTitled Conversation" );
            conversation.setConversationSummary(conversationSummary);

            Conversation saveConversation = conversationRepository.save(conversation);

            Ticket savedticket = iTicketService.createTicketForConversation(conversation);
            saveConversation.setTicketCreated(true);
            saveConversation.setTicket(savedticket);
            conversationRepository.save(saveConversation);

            //send email notification to the customer
            //remove thr conversation from memory
            activeConversation.remove(sessionId);

            return savedticket;

        }catch(Exception e){
            String errorMsg="Error occurred during conversation creation" + e.getMessage();
            webSocketMessageSender.sendMessageToUser(sessionId,errorMsg);
            return null;

        }


        //return null;
        //ask Ai to summarize customer conversation
        //ASK AI to generate a suitable time for conversation
    }

    private static Conversation getConversation(Customer customer){
        Conversation conversation = new Conversation();
        conversation.setCustomer(customer);
        conversation.setTicketCreated(false);
        return conversation;

    }

    private Customer getCustomerInformation(List<ChatEntry> history){
        CustomerInfo customerInfo = CustomerInfoHelper.extractUserInformationChatHistory(history);
        log.info("CustomerInfo : {}",customerInfo);
        Customer customer= userRepository.findByEmailAddressAndPhoneNumber(customerInfo.emailAddress(),customerInfo.phoneNumber());
        return customer;

    }
}
