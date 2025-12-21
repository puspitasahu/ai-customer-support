package com.dailywork.aicustomersupport.controller;

import com.dailywork.aicustomersupport.dtos.ChatMessageDto;
import com.dailywork.aicustomersupport.service.chat.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/chat")
public class ChatController {
    private final ChatClient chatClient;
    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<String> handleCharMessage(@RequestBody ChatMessageDto message){
        String response = conversationService.handleMessage(message);
        log.info("The response from controller {}",response);
        return ResponseEntity.ok(response);    }

}
