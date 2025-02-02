package com.ltev.connected.dao;

import com.ltev.connected.domain.Message;
import com.ltev.connected.domain.User;

import java.util.List;

public interface MessageDao {

    List<Message> getLastMessages(User user);
}