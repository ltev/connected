package com.ltev.connected.dao.impl.helper;

import com.ltev.connected.domain.Message;
import com.ltev.connected.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@AllArgsConstructor
public class LastMessageInfoRowMapper implements RowMapper<Message> {

    private User user;

    // fu.id as from_user_id, fu.username as from_user_username, tu.id as to_user_id, tu.username as to_user_username, created, text
    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long fromId = rs.getLong("from_user_id");
        Long toId = rs.getLong("to_user_id");
        ZonedDateTime created = rs.getTimestamp("created").toLocalDateTime().atZone(ZoneId.systemDefault());
        String text = rs.getString("text");

        Message lastMessage = new Message();
        Message.Id id;

        if (fromId == user.getId()) {
            id = new Message.Id(
                    user,
                    new User(rs.getLong("to_user_id"), rs.getString("to_user_username")),
                    created
            );
        } else if (toId == user.getId()) {
            id = new Message.Id(
                    new User(rs.getLong("from_user_id"), rs.getString("from_user_username")),
                    user,
                    created
            );
        } else {
            throw new RuntimeException("Invalid id");
        }

        lastMessage.setId(id);
        lastMessage.setText(text);

        return lastMessage;
    }
}
