package com.dailywork.aicustomersupport.service.Ticket;

import com.dailywork.aicustomersupport.dtos.TicketDTO;
import com.dailywork.aicustomersupport.enums.TicketStatus;
import com.dailywork.aicustomersupport.model.Conversation;
import com.dailywork.aicustomersupport.model.Ticket;
import com.dailywork.aicustomersupport.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class TicketService implements ITicketService{
    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;


    @Override
    public Ticket createTicketForConversation(Conversation conversation) {
        Ticket ticket =new Ticket();
        ticket.setConversation(conversation);
        ticket.setTicketStatus(TicketStatus.OPENED);
        ticket.setResolvedAt(null);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setReferenceNumber(generateRandomAlphaNumeric(10));
        return ticketRepository.save(ticket);
    }

    @Override
    public TicketDTO getTicketId(Long ticketId) {

        return ticketRepository.findById(ticketId)
                .map(ticket -> objectMapper.convertValue(ticket,TicketDTO.class))
                .orElseThrow(()->new EntityNotFoundException("Ticket with id" + ticketId + "Not Found"));
    }

    @Override
    public TicketDTO resolveTicket(Long ticketId, String resolutionDetails) {

        return null;
    }

    @Override
    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(ticket->objectMapper.convertValue(ticket,TicketDTO.class))
                .toList();
    }

    private String generateRandomAlphaNumeric(int length){
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0','z')
                .filteredBy(Character::isLetterOrDigit)
                .get();

        return generator.generate(length).toUpperCase();
    }
}
