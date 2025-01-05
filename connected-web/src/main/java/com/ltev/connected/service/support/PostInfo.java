package com.ltev.connected.service.support;

import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostInfo {

    private User loggedUser;
    private Post post;
    private boolean friends;

    public boolean isUserLogged() {
        return loggedUser != null;
    }

    public boolean isSelfPost() {
        return loggedUser.equals(post.getUser());
    }
}