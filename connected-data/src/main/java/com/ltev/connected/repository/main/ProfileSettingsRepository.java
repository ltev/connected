package com.ltev.connected.repository.main;

import com.ltev.connected.domain.ProfileSettings;
import com.ltev.connected.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileSettingsRepository extends JpaRepository<ProfileSettings, User> {

    Optional<ProfileSettings> findByUserUsername(String username);
}