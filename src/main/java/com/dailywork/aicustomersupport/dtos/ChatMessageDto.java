package com.dailywork.aicustomersupport.dtos;

import lombok.Data;

@Data
public class ChatMessageDto {
    private String sessionId;
    private String message;
}
