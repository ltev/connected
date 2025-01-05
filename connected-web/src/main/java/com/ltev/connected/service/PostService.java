package com.ltev.connected.service;

import com.ltev.connected.domain.Post;
import com.ltev.connected.service.support.PostInfo;

import java.util.List;

public interface PostService {
    void save(Post post);

    List<Post> findFriendsPosts(String username);

    List<Post> findPosts(Long userId, Post.Visibility visibility);

    PostInfo getPostInfo(Long postId);
}