package com.ltev.connected.repository;

import com.ltev.connected.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Like.Id> {
}