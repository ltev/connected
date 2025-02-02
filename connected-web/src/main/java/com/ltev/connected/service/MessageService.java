package com.ltev.connected.service;

import com.ltev.connected.domain.Message;
import com.ltev.connected.dto.MessagesInfo;

public interface MessageService {

    void saveMessage(Message message, Long profileId);

    MessagesInfo getMessagesInfo(String profileUsername);
}