package com.dailywork.aicustomersupport.service.chat;

import com.dailywork.aicustomersupport.dtos.ChatMessageDto;
import reactor.core.publisher.Mono;

public interface IConversationService {
    String handleMessage(ChatMessageDto message);
}
