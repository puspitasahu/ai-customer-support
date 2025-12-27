package com.dailywork.aicustomersupport.repository;

import com.dailywork.aicustomersupport.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation,Long> {
}
