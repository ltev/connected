package com.ltev.connected.repository.main;

import com.ltev.connected.config.ConnectedDatabaseConfiguration;
import com.ltev.connected.config.FlywayConfiguration;
import com.ltev.connected.domain.Message;
import com.ltev.connected.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;

import java.time.ZonedDateTime;

@DataJpaTest
@Import({ConnectedDatabaseConfiguration.class, FlywayConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void testSave() {
        Message message = new Message();
        message.setId(new Message.Id(new User(10L), new User(13L), ZonedDateTime.now()));
        message.setText("test text");

        messageRepository.save(message);
    }
}