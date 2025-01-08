package com.ltev.connected.service;

import com.ltev.connected.domain.Like;
import com.ltev.connected.domain.Post;
import com.ltev.connected.dto.PostInfo;

import java.util.List;
import java.util.Optional;

public interface PostService {
    void savePost(Post post);

    List<Post> findFriendsPosts(String username);

    List<PostInfo> findPostsInfo();

    List<PostInfo> findFriendsPostsInfo();

    Optional<PostInfo> getPostInfo(Long postId);

    void saveComment(Long postId, String comment, Long loggedUserId);

    void saveOrRemoveLike(Long postId, Like.Value likeValue);
}