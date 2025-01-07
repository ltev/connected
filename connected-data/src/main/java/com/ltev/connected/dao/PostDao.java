package com.ltev.connected.dao;

import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;

import java.util.List;
import java.util.Optional;

public interface PostDao {

    Optional<Post> findById(Long postId);

    void save(Post post);

    List<Post> findPosts(Long userId, Post.Visibility visibility);

    List<Post> findPosts(User user, List<Post.Visibility> visibilities);

    List<Post> findFriendsPosts(String username, List<Post.Visibility> visibilities);

    void increaseNumCommentsByOne(Long postId);
}