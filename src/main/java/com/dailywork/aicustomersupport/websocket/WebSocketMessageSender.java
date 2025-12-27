package com.dailywork.aicustomersupport.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketMessageSender {
    private final SimpMessagingTemplate messaginTemplate;

    public void sendMessageToUser(String sessionId,String message){
        String destination ="/topic/message"+sessionId;
        messaginTemplate.convertAndSend(destination,message);
    }

}
