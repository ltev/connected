package com.ltev.connected.service;

import com.ltev.connected.domain.Message;
import com.ltev.connected.dto.ConversationInfo;

public interface MessageService {

    void saveMessage(Message message, Long profileId);

    ConversationInfo getMessagesInfo(String profileUsername);
}