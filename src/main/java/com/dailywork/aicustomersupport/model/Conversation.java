package com.dailywork.aicustomersupport.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Conversation {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String conversationTitle;
    @Lob
    private String conversationSummary;
    private boolean ticketCreated;
    @ManyToOne
    private User user;
    @OneToOne(mappedBy = "conversation")
    private Ticket ticket;
}
