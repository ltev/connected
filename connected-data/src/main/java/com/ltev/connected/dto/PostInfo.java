package com.ltev.connected.dto;

import com.ltev.connected.domain.Like;
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
    private Like.Value likeValue;
    private int numLikes;
    private int numDislikes;
    private int numIndifferences;

    public boolean isUserLogged() {
        return loggedUser != null;
    }

    public boolean isSelfPost() {
        return post.getUser().equals(loggedUser);
    }
}