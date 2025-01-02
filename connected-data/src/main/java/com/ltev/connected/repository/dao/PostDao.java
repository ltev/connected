package com.ltev.connected.repository.dao;

import com.ltev.connected.domain.Post;

import java.util.List;

public interface PostDao {

    List<Post> findFriendsPosts(Long userId);

    void save(Post post);
}
