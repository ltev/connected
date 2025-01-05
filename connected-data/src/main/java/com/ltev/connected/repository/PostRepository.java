package com.ltev.connected.repository;

import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserAndVisibility(User user, Post.Visibility visibility);

    List<Post> findAllByUserAndVisibilityIn(User user, Collection<Post.Visibility> visibilities);
}