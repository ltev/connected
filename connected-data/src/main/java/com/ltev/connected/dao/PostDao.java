package com.ltev.connected.dao;

import com.ltev.connected.domain.Comment;
import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.PostInfo;

import java.util.List;
import java.util.Optional;

public interface PostDao {

    Optional<Post> findById(Long postId);

    void save(Post post);

    List<Post> findPosts(Long userId, Post.Visibility visibility);

    List<Post> findPosts(User user, List<Post.Visibility> visibilities);

    List<Post> findFriendsPosts(String username, List<Post.Visibility> visibilities);

    Optional<PostInfo> findPostInfo(Long postId, String loggedUsername);

    List<PostInfo> findPostsInfo(String username);

    List<PostInfo> findPostsInfo(String postUsername, List<Post.Visibility> visibilities, String loggedUsername);

    List<PostInfo> findFriendsPostsInfo(String username);

    void increaseNumCommentsByOne(Long postId);

    List<Comment> findCommentsByPost(Long postId);
}