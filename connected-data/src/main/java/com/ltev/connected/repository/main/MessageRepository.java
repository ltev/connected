package com.ltev.connected.repository.main;

import com.ltev.connected.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Message.Id> {
}