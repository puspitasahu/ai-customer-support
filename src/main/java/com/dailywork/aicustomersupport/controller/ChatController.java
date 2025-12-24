package com.dailywork.aicustomersupport.controller;

import com.dailywork.aicustomersupport.dtos.ChatMessageDto;
import com.dailywork.aicustomersupport.service.chat.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/chat")
public class ChatController {
    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<String> handleCharMessage(@RequestBody ChatMessageDto message){
        String response = conversationService.handleMessage(message);
        log.info("The response from controller {}",response);
        return ResponseEntity.ok(response);
    }

}
