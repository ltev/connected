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
import java.util.List;

@Primary
@Repository
@AllArgsConstructor
public class PostDaoImpl implements PostDao {

    private final PostRepository postRepository;
    private final EntityManagerFactory emf;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public List<Post> findFriendsPosts(Long userId) {
        String sql = """
                select p.id as post_id, p.created, p.visibility, p.text, u.id as user_id, u.username from posts p
                join users u on u.id = user_id
                where user_id in (
                	select to_user_id
                	from friend_requests
                	Where from_user_id = ? and accepted is not null
                	UNION
                	select from_user_id
                	from friend_requests
                	where to_user_id = ? and accepted is not null
                )
                """;
            return jdbcTemplate.query(sql,
                    (ResultSet rs, int rowNum) -> {
                        User user = new User();
                        user.setId(rs.getLong("user_id"));
                        user.setUsername(rs.getString("username"));

                        Post post = new Post();
                        post.setUser(user);
                        post.setId(rs.getLong("post_id"));
                        post.setCreated(rs.getTimestamp("created").toInstant().atZone(ZoneId.systemDefault()));
                        post.setText(rs.getString("text"));
                        post.setVisibility(Post.Visibility.ofOrdinal(rs.getByte("visibility")));

                        return post;
                    }, userId, userId);
    }

    // == PRIVATE HELPER METHODS

    private List<Long> findFriends(Long userId) {
        try (var em = emf.createEntityManager()) {
            return em.createNativeQuery(
                            """
                                    select to_user_id
                                    from friend_requests
                                    Where from_user_id = ?1 and accepted is not null
                                    UNION
                                    select from_user_id
                                    from friend_requests
                                    Where to_user_id = ?1 and accepted is not null""")
                    .setParameter(1, userId)
                    .getResultList();
        }
    }

}
