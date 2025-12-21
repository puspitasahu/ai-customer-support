package com.dailywork.aicustomersupport.service.chat;

import com.dailywork.aicustomersupport.dtos.ChatEntry;
import com.dailywork.aicustomersupport.dtos.ChatMessageDto;
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
                return confirmationMessage;
            }catch(Exception e){
                return "Error generating confirmation message";
            }

        }

        history.add(new ChatEntry("assistant",aiResponseText));
        return  aiResponseText;

    }
}
