package com.ltev.connected.dao.impl;

import com.ltev.connected.dao.PostDao;
import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.PostRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
@AllArgsConstructor
public class PostDaoImpl implements PostDao {

    public static final String FIND_FRIENDS_IDS_BY_USERNAME_SQL = """
            select to_user_id
            from friend_requests
            Where from_user_id = (select id from users where username = ?) and accepted is not null
            UNION
            select from_user_id
            from friend_requests
            Where to_user_id = (select id from users where username = ?) and accepted is not null""";

    public static final String FIND_FRIENDS_POSTS_BY_USERNAME_AND_VISIBILITY_SQL = """
            select p.id as post_id, p.created, p.visibility, p.title, p.text, p.num_comments, u.id as user_id, u.username
            from posts p
            join users u on u.id = user_id
            where user_id in ("""
            + FIND_FRIENDS_IDS_BY_USERNAME_SQL
            + ") and visibility in ";

    private final PostRepository postRepository;
    private final EntityManagerFactory emf;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    public List<Post> findFriendsPosts(String username, List<Post.Visibility> visibilities) {
        String visibilitiesSql = String.join(",", Collections.nCopies(visibilities.size(), "?"));
        String sql = new StringBuilder()
                .append(FIND_FRIENDS_POSTS_BY_USERNAME_AND_VISIBILITY_SQL)
                .append("(")
                .append(visibilitiesSql)
                .append(")")
                .toString();

        Object[] parameters = new Object[2 + visibilities.size()];
        parameters[0] = username;
        parameters[1] = username;
        for (int i = 0; i < visibilities.size(); i++) {
            parameters[2 + i] = visibilities.get(i).ordinal();
        }

        return jdbcTemplate.query(sql,
                (ResultSet rs, int rowNum) -> {
                    User user = new User();
                    user.setId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));

                    Post post = new Post();
                    post.setUser(user);
                    post.setCreated(rs.getTimestamp("created").toInstant().atZone(ZoneId.systemDefault()));
                    post.setVisibility(Post.Visibility.ofOrdinal(rs.getByte("visibility")));
                    post.setId(rs.getLong("post_id"));
                    post.setTitle(rs.getString("title"));
                    post.setText(rs.getString("text"));
                    post.setNumComments(rs.getLong("num_comments"));

                    return post;
                }, parameters);
    }

    @Override
    public List<Post> findPosts(Long userId, Post.Visibility visibility) {
        return postRepository.findAllByUserAndVisibility(new User(userId), visibility);
    }

    @Override
    public List<Post> findPosts(User user, List<Post.Visibility> visibilities) {
        return postRepository.findAllByUserAndVisibilityIn(user, visibilities);
    }

    @Override
    public void increaseNumCommentsByOne(Long postId) {
//        String sql = "update posts set num_comments = num_comments + 1 where id = ?";
//        jdbcTemplate.update(sql, postId);
        postRepository.increaseNumCommentsByOne(postId);
    }

    // == PRIVATE HELPER METHODS

    private List<Long> findFriends(Long userId) {
        try (var em = emf.createEntityManager()) {
            return em.createNativeQuery(
                            FIND_FRIENDS_IDS_BY_USERNAME_SQL)
                    .setParameter(1, userId)
                    .getResultList();
        }
    }
}