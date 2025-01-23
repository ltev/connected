package com.ltev.connected.repository.main;

import com.ltev.connected.domain.Comment;
import com.ltev.connected.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    @Transactional
    void deleteByUserId(Long userId);

    @Transactional
    void deleteByPostUserId(Long userId);
}