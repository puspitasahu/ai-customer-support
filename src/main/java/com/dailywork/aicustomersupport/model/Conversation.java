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
    @Column(columnDefinition = "TEXT")
    private String conversationSummary;
    private boolean ticketCreated;
    @ManyToOne
    private Customer customer;
    @OneToOne(mappedBy = "conversation")
    private Ticket ticket;
}
