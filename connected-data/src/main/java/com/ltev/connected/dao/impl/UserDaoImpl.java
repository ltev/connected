package com.ltev.connected.dao.impl;

import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private static class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            return user;
        }
    }

    private static final String FIND_FRIENDS_SQL = """
            select id, username from users
            where id in (
            	select to_user_id
            	from friend_requests
            	Where from_user_id = ? and accepted is not null
            	UNION
            	select from_user_id
            	from friend_requests
            	where to_user_id = ? and accepted is not null
            )""";

    private JdbcTemplate jdbcTemplate;

    private UserRepository repository;

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Optional<Long> findIdByUsername(String username) {
        return repository.findIdByUsername(username);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return repository.findById(userId);
    }

    @Override
    public List<User> findAllFriends(Long userId) {
        return jdbcTemplate.query(FIND_FRIENDS_SQL, new UserRowMapper(), userId, userId);
    }
}
