package com.ltev.connected.dto;

import com.ltev.connected.domain.Message;
import com.ltev.connected.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class MessagesInfo {

    private User loggedUser;
    private User profileUser;
    Page<Message> page;

    public boolean profileExist() {
        return profileUser != null;
    }
}