package com.ltev.connected.dao.impl;

import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
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

    private static class UserResultSetExtractor implements ResultSetExtractor<User> {

        /**
         * ResultSet Data:
         * u.id as user_id, u.username, p.id as post_id, p.created, p.visibility, p.text
         */
        @Override
        public User extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next() == false) {
                throw new RuntimeException("Result set should not be empty!");
            }
            User user = new User(rs.getLong(1));
            user.setUsername(rs.getString(2));

            List<Post> posts = new ArrayList<>();

            if (rs.getLong("post_id") != 0) {
                posts.add(extractPost(rs));

                while (rs.next()) {
                    posts.add(extractPost(rs));
                }
            }
            user.setPosts(posts);
            return user;
        }

        private Post extractPost(ResultSet rs) throws SQLException {
            Post post = new Post();
            post.setId(rs.getLong(3));
            post.setCreated(rs.getTimestamp(4).toInstant().atZone(ZoneId.systemDefault()));
            post.setVisibility(Post.Visibility.ofOrdinal(rs.getByte(5)));
            post.setText(rs.getString(6));
            return post;
        }

    }

    private static final String INSERT_INTO_USERS_SQL = "insert into users (username, password, enabled) values (?, ?, 1)";
    private static final String FIND_FRIENDS_BY_USER_ID_SQL = """
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
    public void createNewUser(String username, String password) {
        jdbcTemplate.update(INSERT_INTO_USERS_SQL, username, password);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Optional<User> findByUsernameAndVisibility(String username, List<Post.Visibility> visibilities) {
        String inSql = String.join(",", Collections.nCopies(visibilities.size(), "?"));
        String sql = String.format("""
                select u.id as user_id, u.username, p.id as post_id, p.created, p.visibility, p.text
                from users u
                left join (
                	select * from posts
                    where user_id = (select id from users where username = ?)
                    and visibility in (%s)
                    ) p
                    on u.id = p.user_id
                where u.username = ?""", inSql);

        Object[] parameters = new Object[visibilities.size() + 2];
        parameters[0] = username;
        parameters[parameters.length - 1] = username;
        int i = 1;
        for (Post.Visibility visibility : visibilities) {
            parameters[i++] = visibility.ordinal();
        }
        return Optional.of(jdbcTemplate.query(sql, new UserResultSetExtractor(), parameters));
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
        return jdbcTemplate.query(FIND_FRIENDS_BY_USER_ID_SQL, new UserRowMapper(), userId, userId);
    }
}
