package com.dailywork.aicustomersupport.service.chat;

import com.dailywork.aicustomersupport.dtos.ChatEntry;
import com.dailywork.aicustomersupport.dtos.ChatMessageDto;
import com.dailywork.aicustomersupport.event.TicketCreationEvent;
import com.dailywork.aicustomersupport.helper.CustomerInfo;
import com.dailywork.aicustomersupport.helper.CustomerInfoHelper;
import com.dailywork.aicustomersupport.helper.RegexPattern;
import com.dailywork.aicustomersupport.model.Conversation;
import com.dailywork.aicustomersupport.model.Ticket;
import com.dailywork.aicustomersupport.model.Customer;
import com.dailywork.aicustomersupport.repository.ConversationRepository;
import com.dailywork.aicustomersupport.repository.TicketRepository;
import com.dailywork.aicustomersupport.repository.UserRepository;
import com.dailywork.aicustomersupport.service.Ticket.ITicketService;
import com.dailywork.aicustomersupport.websocket.WebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService implements IConversationService {
    private final AISupportService aiSupportService;
    private final UserRepository userRepository;
    private final WebSocketMessageSender webSocketMessageSender;
    private final ApplicationEventPublisher publisher;
    private final ConversationRepository conversationRepository;
    private final ITicketService iTicketService;
    private final TicketRepository ticketRepository;
    private Map<String, List<ChatEntry>> activeConversations = new ConcurrentHashMap<>();

    //This will handle case when user enters wrong contact information
    private Map<String,Boolean> waitingForContactCorrection = new ConcurrentHashMap<>();
    public String handleMessage(ChatMessageDto chatMessage) {
        String sessionId = chatMessage.getSessionId();
        String message = chatMessage.getMessage()!=null ?chatMessage.getMessage().trim():"";
        log.info("The Session Id is {}",sessionId);
        log.info("The Messgae content is{}", message);

        List<ChatEntry> history = activeConversations.computeIfAbsent(sessionId, k-> Collections.synchronizedList(new ArrayList<>()));
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
        log.info("aiResponseText :: {}",aiResponseText);

        if(aiResponseText.contains("TICKET_CREATION_READY")){
            try{
                String confirmationMessage = aiSupportService.generateUserConfirmationMessage().block();
                history.add(new ChatEntry("assistant",confirmationMessage));

                CompletableFuture.runAsync(()->{
                    try{
                        Ticket  tempTicket = finalizeConversationAndCreateTicket(sessionId);
                        if(tempTicket !=null){
                            history.add(new ChatEntry("system","The email has been sent to user"));

                            String feedbackMessage = aiSupportService.generateEmailNotificationMessage().block();
                            if(feedbackMessage!=null){
                                List<ChatEntry> currentHistory = activeConversations.get(sessionId);
                                if(currentHistory!=null){
                                    currentHistory.add(new ChatEntry("assistant",feedbackMessage));
                                }
                                webSocketMessageSender.sendMessageToUser(sessionId, feedbackMessage);
                            }
                        }
                        //Ask AI to generate report and send email to customer




                    } catch (Exception e) {
                        log.error("Error in async ticket creation and notification", e);
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
        List<ChatEntry> history = activeConversations.get(sessionId);
        log.info("The conversation history : {}",history);
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
            waitingForContactCorrection.put(sessionId,true);
            //set up flag and wait for customer information correction
            return  null;
        }
        waitingForContactCorrection.remove(sessionId);

        Conversation  conversation =  getConversation( customer);

        try{

            List<ChatEntry> userConversation = history.stream().filter(entry->"user".equalsIgnoreCase(entry.getRole()))
                    .toList();

            String formattedConversation = userConversation.stream()
                    .map(ChatEntry::getContent)  // Get just the content
                    .collect(Collectors.joining("\n"));
         //   String conversationSummary = aiSupportService.summarizeCustomerConversationMessage(userConversation.toString()).block();
            String conversationSummary = aiSupportService.summarizeCustomerConversationMessage(formattedConversation.toString()).block();
            log.info("The conversation history {}", conversationSummary);

            String conversationTitle = aiSupportService.conversationTitle(conversationSummary).block();
            log.info("ConversationTitle :{}",conversationTitle);

            conversation.setConversationTitle(conversationTitle!=null ? conversationTitle.trim() : "UnTitled Conversation" );
            conversation.setConversationSummary(conversationSummary);

            Conversation saveConversation = conversationRepository.save(conversation);

            Ticket ticket = iTicketService.createTicketForConversation(conversation);

            CustomerInfo customerInfo = getCustomerInfo(history);

            if(customerInfo.orderNumber()!=null){
                ticket.setProductOrderNumber(customerInfo.orderNumber());
            }else{
                ticket.setProductOrderNumber(null);
            }

            Ticket savedTicket = ticketRepository.save(ticket);

            saveConversation.setTicketCreated(true);
            saveConversation.setTicket(savedTicket);
            conversationRepository.save(saveConversation);

            //send email notification to the customer
            publisher.publishEvent(new TicketCreationEvent(savedTicket));
            //remove thr conversation from memory
            activeConversations.remove(sessionId);

            return savedTicket;

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

    private static CustomerInfo getCustomerInfo(List<ChatEntry> history){
        CustomerInfo customerInfo = CustomerInfoHelper.extractUserInformationChatHistory(history);
        return customerInfo;
    }

    private void replaceOldContactInformationHistory(List<ChatEntry> history, String emailAddress,String phone){
        if(history == null || history.isEmpty()){
            return;
        }

        createCustomerInformation( history,emailAddress, RegexPattern.EMAIl_PATTERN);
        createCustomerInformation(history,phone,RegexPattern.PHONE_PATTERN);

    }

    private void createCustomerInformation(List<ChatEntry> history, String newContact, Pattern pattern){

        if(history == null || history.isEmpty()){
            return;
        }
        OptionalInt indexOpt = IntStream.range(0, history.size())
                .filter(i->"user".equalsIgnoreCase(history.get(i).getRole())
                && pattern.matcher(history.get(i).getContent())
                        .find())
                .findFirst();

        indexOpt.ifPresentOrElse(
                idx-> history.set(idx,new ChatEntry("user",newContact)),
                (()->history.add(new ChatEntry("user",newContact)))
        );

    }
}
