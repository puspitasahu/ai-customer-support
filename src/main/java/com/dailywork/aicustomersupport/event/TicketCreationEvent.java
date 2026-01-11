package com.dailywork.aicustomersupport.event;

import com.dailywork.aicustomersupport.model.Ticket;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class TicketCreationEvent extends ApplicationEvent {
    private final Ticket ticket;
    public TicketCreationEvent(Ticket ticket){
        super(ticket);
        this.ticket=ticket;
    }

}
