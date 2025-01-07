package com.ltev.connected.service;

import com.ltev.connected.domain.Like;
import com.ltev.connected.domain.Post;
import com.ltev.connected.service.support.PostInfo;

import java.util.List;

public interface PostService {
    void savePost(Post post);

    List<Post> findFriendsPosts(String username);

    PostInfo getPostInfo(Long postId);

    void saveComment(Long postId, String comment, Long loggedUserId);

    void saveOrRemoveLike(Long postId, Like.Value likeValue);
}