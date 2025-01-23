package com.ltev.connected.repository.userData.impl;

import com.ltev.connected.domain.UserDetailsView;
import com.ltev.connected.repository.userData.UserDetailsViewRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsViewRepositoryImpl extends UserDetailsViewRepository, JpaRepository<UserDetailsView, Long> {
}