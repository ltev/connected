package com.ltev.connected.domain;

import com.ltev.connected.converter.PasswordConverter;
import com.ltev.connected.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PasswordConverter.class)
class UserDbTest {

    @Autowired
    UserRepository userRepository;

    @Transactional
    @Rollback
    @Test
    void insertTest() {
        ProfileSettings profileSettings = new ProfileSettings();
        profileSettings.setFriendsVisibility(Visibility.FRIENDS);
        profileSettings.setGroupsVisibility(Visibility.FRIENDS);

        User user = new User();
        user.setUsername("zxczxzq1a231123");
        user.setPassword("password");
        user.setEnabled((byte) 1);
        user.setProfileSettings(profileSettings);

        // persist
        userRepository.save(user);
        assertThat(user.getPassword()).startsWith("{bcrypt}");

        // update
        user.setPassword("passwordAgain");
        user = userRepository.saveAndFlush(user);
        assertThat(user.getPassword()).startsWith("{bcrypt}");
    }
}