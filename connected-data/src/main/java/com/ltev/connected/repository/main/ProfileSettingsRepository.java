package com.ltev.connected.repository.main;

import com.ltev.connected.domain.ProfileSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProfileSettingsRepository extends JpaRepository<ProfileSettings, Long> {

    Optional<ProfileSettings> findByUserUsername(String username);

    @Transactional
    @Modifying
    @Query("delete from ProfileSettings where id = ?1")
    @Override
    void deleteById(Long userId);           // why doesn't it work implicitly ???
}