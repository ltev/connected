package com.ltev.connected.repository.main;

import com.ltev.connected.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface LikeRepository extends JpaRepository<Like, Like.Id> {

    @Transactional
    void deleteByIdUserId(Long userId);

    @Transactional
    void deleteByIdPostUserId(Long userId);
}