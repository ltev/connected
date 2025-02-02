package com.ltev.connected.service.impl;

import com.ltev.connected.dao.MessageDao;
import com.ltev.connected.domain.Message;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.ConversationInfo;
import com.ltev.connected.dto.MessagesInfo;
import com.ltev.connected.repository.main.MessageRepository;
import com.ltev.connected.repository.main.UserRepository;
import com.ltev.connected.service.MessageService;
import com.ltev.connected.service.validator.Validator;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;
    private MessageDao messageDao;
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
    public ConversationInfo getConversationInfo(String profileUsername) {
        ConversationInfo messagesInfo = new ConversationInfo();

        Optional<User> profileUser = userRepository.findByUsername(profileUsername);
        if (profileUser.isEmpty()) {
            return messagesInfo;
        }
        User loggedUser = userRepository.findByUsername(AuthenticationUtils.checkAuthenticationOrThrow().getName()).get();
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("id.created")));
        Page<Message> messagePage = messageRepository.findByUsers(loggedUser, profileUser.get(), pageable);


        messagesInfo.setLoggedUser(loggedUser);
        messagesInfo.setProfileUser(profileUser.get());
        messagesInfo.setPage(getPageWithReveredContent(messagePage));

        return messagesInfo;
    }

    @Override
    public MessagesInfo getMessagesInfo() {
        User loggedUser = userRepository.findByUsername(AuthenticationUtils.checkAuthenticationOrThrow().getName()).get();

        MessagesInfo messagesInfo = new MessagesInfo();
        messagesInfo.setLoggedUser(loggedUser);
        messagesInfo.setMessages(messageDao.getLastMessages(loggedUser));
        messagesInfo.removeLastPreviousMessage();

        return messagesInfo;
    }

    private <T> Page<T> getPageWithReveredContent(Page<T> page) {
        return new Page<T>() {

            private Page<T> originalPage = page;

            @Override
            public int getTotalPages() {
                return page.getTotalPages();
            }

            @Override
            public long getTotalElements() {
                return page.getTotalElements();
            }

            @Override
            public <U> Page<U> map(Function<? super T, ? extends U> converter) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getNumber() {
                return page.getNumber();
            }

            @Override
            public int getSize() {
                return page.getSize();
            }

            @Override
            public int getNumberOfElements() {
                return page.getNumberOfElements();
            }

            @Override
            public List<T> getContent() {
                ArrayList<T> list = new ArrayList<>(page.getContent());
                Collections.reverse(list);
                return list;
            }

            @Override
            public boolean hasContent() {
                return page.hasContent();
            }

            @Override
            public Sort getSort() {
                return page.getSort();
            }

            @Override
            public boolean isFirst() {
                return page.isFirst();
            }

            @Override
            public boolean isLast() {
                return page.isLast();
            }

            @Override
            public boolean hasNext() {
                return page.hasNext();
            }

            @Override
            public boolean hasPrevious() {
                return page.hasPrevious();
            }

            @Override
            public Pageable nextPageable() {
                return page.nextPageable();
            }

            @Override
            public Pageable previousPageable() {
                return page.previousPageable();
            }

            @Override
            public Iterator<T> iterator() {
                return page.iterator();
            }
        };
    }
}