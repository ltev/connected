package com.ltev.connected.dao.impl;

import com.ltev.connected.dao.MessageDao;
import com.ltev.connected.dao.impl.helper.LastMessageInfoRowMapper;
import com.ltev.connected.domain.Message;
import com.ltev.connected.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class MessageDaoImpl implements MessageDao {

    private JdbcTemplate jdbcTemplate;

    /**
     *
     * @param user
     * @return List of messages - for each user one last conversation message
     */
    @Override
    public List<Message> getLastMessages(User user) {
        String sql = """
                select fu.id as from_user_id, fu.username as from_user_username, tu.id as to_user_id, tu.username as to_user_username, created, text
                from (select *
                	from (select *
                		from messages m
                		where (m.from_user_id = ? and to_user_id in
                			(
                				select m.to_user_id from messages m where m.from_user_id = ? group by m.to_user_id
                				union
                				select m.from_user_id from messages m where m.to_user_id = ? group by m.from_user_id
                			))
                			or (m.to_user_id = ? and from_user_id in (
                				select m.to_user_id from messages m where m.from_user_id = ? group by m.to_user_id
                				union
                				select m.from_user_id from messages m where m.to_user_id = ? group by m.from_user_id
                			))
                		group by m.from_user_id, m.to_user_id, created
                		order by created desc) as t
                	group by t.from_user_id, t.to_user_id
                	) as t
                left join users fu on fu.id = t.from_user_id
                left join users tu on tu.id = t.to_user_id
                order by created desc""";
        Long userId = user.getId();
        return jdbcTemplate.query(sql, new LastMessageInfoRowMapper(user), userId, userId, userId, userId, userId, userId);
    }
}