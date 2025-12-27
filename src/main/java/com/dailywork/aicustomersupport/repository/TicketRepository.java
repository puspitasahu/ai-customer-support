package com.dailywork.aicustomersupport.repository;

import com.dailywork.aicustomersupport.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket,Long> {

}
