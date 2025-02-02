package com.ltev.connected.repository.main;

import com.ltev.connected.domain.Message;
import com.ltev.connected.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Message.Id> {

    @Query("select m from Message m where (m.id.fromUser = ?1 and m.id.toUser = ?2) or (m.id.fromUser = ?2 and m.id.toUser = ?1)")
    Page<Message> findByUsers(User u1, User u2, Pageable pageable);
}