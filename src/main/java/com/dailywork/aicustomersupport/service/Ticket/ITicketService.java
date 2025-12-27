package com.dailywork.aicustomersupport.service.Ticket;

import com.dailywork.aicustomersupport.dtos.TicketDTO;
import com.dailywork.aicustomersupport.model.Conversation;
import com.dailywork.aicustomersupport.model.Ticket;

import java.util.List;

public interface ITicketService {
    Ticket createTicketForConversation(Conversation conversation);
    TicketDTO getTicketId(Long ticketId);
    TicketDTO resolveTicket(Long ticketId,String resolutionDetails);
    List<TicketDTO> getAllTickets();
}
