package com.ltev.connected.service;

import com.ltev.connected.domain.Comment;
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

    Optional<PostInfo> getPostInfoForPersonPost(Long postId);

    Optional<PostInfo> getPostInfoForGroupPost(Long postId);

    void saveComment(Comment comment);

    void saveOrRemoveLike(Long postId, Like.Value likeValue);
}