package com.dailywork.aicustomersupport.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketMessageSender {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessageToUser(String sessionId,String message){
        String destination ="/topic/message/"+sessionId;
        log.info("Destination: {}", destination);

        try {
            messagingTemplate.convertAndSend(destination, message);
            log.info("Message sent successfully to: {}", destination);
        } catch (Exception e) {
            log.error("Failed to send WebSocket message", e);
        }
    }

}
