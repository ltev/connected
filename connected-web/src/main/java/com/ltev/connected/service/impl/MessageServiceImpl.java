package com.ltev.connected.service.impl;

import com.ltev.connected.domain.Message;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.MessagesInfo;
import com.ltev.connected.repository.main.MessageRepository;
import com.ltev.connected.repository.main.UserRepository;
import com.ltev.connected.service.MessageService;
import com.ltev.connected.service.validator.Validator;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;
    private UserRepository userRepository;

    @Override
    public void saveMessage(Message message, Long profileId) {
        String username = AuthenticationUtils.checkAuthenticationOrThrow().getName();

        User loggedUser = userRepository.findByUsername(username).get();
        if (message.getId() == null) {
            Message.Id id = new Message.Id(loggedUser, new User(profileId), ZonedDateTime.now());
            message.setId(id);
        }

        Validator<Message> messageValidator = new Validator<>(message);
        messageValidator.validate();
        if (messageValidator.hasErrors()) {
            throw new RuntimeException("Validator error: " + messageValidator);
        }

        messageRepository.save(message);
    }

    @Override
    public MessagesInfo getMessagesInfo(String profileUsername) {
        MessagesInfo messagesInfo = new MessagesInfo();
        Optional<User> profileUser = userRepository.findByUsername(profileUsername);
        if (profileUser.isEmpty()) {
            return messagesInfo;
        }

        User loggedUser = userRepository.findByUsername(AuthenticationUtils.checkAuthenticationOrThrow().getName()).get();
        messagesInfo.setLoggedUser(loggedUser);
        messagesInfo.setProfileUser(profileUser.get());
        return messagesInfo;
    }
}