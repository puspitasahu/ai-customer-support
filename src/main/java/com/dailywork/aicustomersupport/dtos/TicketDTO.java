package com.dailywork.aicustomersupport.dtos;

import com.dailywork.aicustomersupport.enums.TicketStatus;
import com.dailywork.aicustomersupport.model.Conversation;

import java.time.LocalDateTime;

public class TicketDTO {
    private Long id;
    private TicketStatus ticketStatus;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private Long productOrderNumber;
    private String referenceNumber;
    private String resolutionDetails;
    private Conversation conversation;

}
