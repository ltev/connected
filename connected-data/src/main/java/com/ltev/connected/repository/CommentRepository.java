package com.ltev.connected.repository;

import com.ltev.connected.domain.Comment;
import com.ltev.connected.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);
}