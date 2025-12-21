package com.dailywork.aicustomersupport.service.chat;

import com.dailywork.aicustomersupport.dtos.ChatEntry;
import com.dailywork.aicustomersupport.utils.PromptTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AISupportService {
    private final  ChatClient chatClient;
    public Mono<String> chatWithHistory(List<ChatEntry>history){
        List<Message> messages= new ArrayList<>();
        messages.add(new SystemMessage(PromptTemplate.SUPPORT_PROMPT_TEMPLATE));
        for(ChatEntry entry:history){
            String content= entry.getContent();
            String role = entry.getRole();
            switch(role){
                case "user" -> messages.add(new UserMessage(content));
                case "assistant" ->messages.add(new AssistantMessage(content));
            }
        }

        return Mono.fromCallable(()->{
            ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
                    .messages(messages)
                    .call();
            String content = responseSpec.content();
            if(content == null){
                throw new IllegalStateException("No Content available");
            }
            return content;
        }).subscribeOn(Schedulers.boundedElastic());

    }

    public Mono<String> generateUserConfirmationMessage(){
        List<Message> messages= new ArrayList<>();
        messages.add(new SystemMessage(PromptTemplate.USER_CONFIRMATION_PROMPT_TEMPLATE));
        return Mono.fromCallable(()->{
            ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
                    .messages(messages)
                    .call();
            String content = responseSpec.content();
            if(content == null){
                throw new IllegalStateException("AI response content is null");
            }
            return content;
        }).subscribeOn(Schedulers.boundedElastic());

    }
}
