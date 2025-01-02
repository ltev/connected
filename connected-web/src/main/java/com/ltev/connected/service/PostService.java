package com.ltev.connected.service;

import com.ltev.connected.domain.Post;

import java.util.List;

public interface PostService {
    void save(Post post);

    List<Post> findFriendsPosts(String username);
}
