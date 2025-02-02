package com.ltev.connected.dto;

import com.ltev.connected.domain.Message;
import com.ltev.connected.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.util.*;

@Getter
@Setter
@ToString
public class MessagesInfo {

    private User loggedUser;
    private List<Message> messages = new ArrayList<>();

    public User getNotLoggedUser(Message message) {
        var id = message.getId();
        if (id.getFromUser().getId() != loggedUser.getId() && id.getToUser().getId() != loggedUser.getId()) {
            throw new RuntimeException("Both users are not 'loggedUser'");
        }
        return id.getFromUser().getId() != loggedUser.getId()
                ? id.getFromUser()
                : id.getToUser();
    }

    /**
     * In list there can be messages
     * from a to b, and from b to a
     * This method deletes the one that was created earlier
     */
    public void removeLastPreviousMessage() {
        Map<User, Message> map = new HashMap<>();
        for (Message message : messages) {
            User notLogged = getNotLoggedUser(message);
            System.out.println(notLogged);
            map.merge(notLogged, message, (m1, m2) -> Duration.between(m1.getId().getCreated(), m2.getId().getCreated()).isPositive() ? m2 : m1);
        }
        messages = new ArrayList<>(map.values());
        messages.sort(Comparator.comparing(m -> m.getId().getCreated()));
        Collections.reverse(messages);
    }
}