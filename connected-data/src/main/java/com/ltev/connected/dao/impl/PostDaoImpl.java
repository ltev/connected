package com.ltev.connected.dao.impl;

import com.ltev.connected.dao.PostDao;
import com.ltev.connected.dao.impl.helper.GroupPostInfoRowMapper;
import com.ltev.connected.dao.impl.helper.PostInfoRowMapper;
import com.ltev.connected.domain.Comment;
import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.PostInfo;
import com.ltev.connected.repository.CommentRepository;
import com.ltev.connected.repository.PostRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
@AllArgsConstructor
public class PostDaoImpl implements PostDao {

    private static final String FIND_FRIENDS_IDS_BY_USERNAME_SQL = """
            select to_user_id
            from friend_requests
            Where from_user_id = (select id from users where username = ?) and accepted is not null
            UNION
            select from_user_id
            from friend_requests
            Where to_user_id = (select id from users where username = ?) and accepted is not null""";

    private static final String FIND_FRIENDS_POSTS_BY_USERNAME_AND_VISIBILITY_SQL = """
            select p.id as post_id, p.created, p.visibility, p.title, p.text, p.num_comments, u.id as user_id, u.username
            from posts p
            join users u on u.id = user_id
            where user_id in ("""
            + FIND_FRIENDS_IDS_BY_USERNAME_SQL
            + ") and visibility in ";

    private static final String FIND_ALL_POSTS_INFO_SQL = """
            select p.id as post_id, p.created, p.visibility, p.title, p.text, p.num_comments,
                	u.id as post_user_id, u.username as post_user_username,
                    (select id from users where username = ?) as user_id, ? as username, l.value as like_value,
                	(select count(*) from likes where post_id = p.id and value = 0) as num_dislike,
                	(select count(*) from likes where post_id = p.id and value = 1) as num_indifference,
                	(select count(*) from likes where post_id = p.id and value = 2) as num_like
                from posts p
                join users u on u.id = p.user_id
                left join likes l on l.post_id = p.id and l.user_id = (select id from users where username = ?)""";

    private final PostRepository postRepository;
    private final EntityManagerFactory emf;
    private final JdbcTemplate jdbcTemplate;
    private final CommentRepository commentRepository;

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

    /**
     * Find one post by its id
     */
    @Override
    public Optional<PostInfo> findPostInfo(Long postId, String loggedUsername) {
        String sql = FIND_ALL_POSTS_INFO_SQL
                + " where p.id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, new PostInfoRowMapper(true),
                loggedUsername, loggedUsername, loggedUsername, postId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    /**
     * Find all posts whom author is 'username'
     */
    @Override
    public List<PostInfo> findPostsInfo(String username) {
        String sql = FIND_ALL_POSTS_INFO_SQL
                + " where p.user_id = (select id from users where username = ?)";
        return jdbcTemplate.query(sql, new PostInfoRowMapper(false),
                username, username, username, username);
    }

    /**
     * Find all posts whom author is 'postUsername', and in visibility
     * loggedUsername -> for checking posts like status
     */
    @Override
    public List<PostInfo> findPostsInfo(String postUsername, List<Post.Visibility> visibilities, String loggedUsername) {
        String visibilitiesSql = String.join(",", Collections.nCopies(visibilities.size(), "?"));

        String sql = new StringBuilder()
                .append(FIND_ALL_POSTS_INFO_SQL)
                .append(" where p.user_id = (select id from users where username = ?)")
                .append(" and p.visibility in (")
                .append(visibilitiesSql)
                .append(")")
                .toString();

        Object[] parameters = new Object[4 + visibilities.size()];
        parameters[0] = loggedUsername;
        parameters[1] = loggedUsername;
        parameters[2] = loggedUsername;
        parameters[3] = postUsername;
        for (int i = 0; i < visibilities.size(); i++) {
            parameters[4 + i] = visibilities.get(i).ordinal();
        }

        return jdbcTemplate.query(sql, new PostInfoRowMapper(false), parameters);
    }

    @Override
    public List<PostInfo> findFriendsPostsInfo(String username) {
        String sql = """
                select p.id as post_id, p.created, p.visibility, p.title, p.text, p.num_comments, 
                u.id as post_user_id, u.username as post_user_username, l.value as like_value,
                	(select count(*) from likes where post_id = p.id and value = 0) as num_dislike,
                	(select count(*) from likes where post_id = p.id and value = 1) as num_indifference,
                	(select count(*) from likes where post_id = p.id and value = 2) as num_like
                from posts p
                join users u on u.id = p.user_id
                left join likes l on l.post_id = p.id and l.user_id = (select id from users where username = ?)
                where p.user_id in (
                	select to_user_id
                	from friend_requests
                	Where from_user_id = (select id from users where username = ?) and accepted is not null
                	UNION
                	select from_user_id
                	from friend_requests
                	where to_user_id = (select id from users where username = ?) and accepted is not null
                ) and visibility in (2, 3, 4)""";
        return jdbcTemplate.query(sql, new PostInfoRowMapper(false), username, username, username);
    }

    @Override
    public List<Post> findPosts(Long userId, Post.Visibility visibility) {
        return postRepository.findAllByUserAndVisibility(new User(userId), visibility);
    }

    @Override
    public List<Post> findPosts(User user, List<Post.Visibility> visibilities) {
        return postRepository.findAllByUserAndVisibilityIn(user, visibilities);
    }

    @Transactional(propagation = Propagation.MANDATORY, label = "Should be called from saving comment transaction scope")
    @Override
    public void increaseNumCommentsByOne(Long postId) {
        String sql = "update posts set num_comments = num_comments + 1 where id = ?";
        jdbcTemplate.update(sql, postId);
        //postRepository.increaseNumCommentsByOne(postId);  //java.sql.SQLSyntaxErrorException: Table 'connected.ht_posts' doesn't exist
    }

    @Override
    public List<Comment> findCommentsByPost(Long postId) {
        return commentRepository.findByPost(new Post(postId));
    }

    @Override
    public Page<PostInfo> findGroupPostsInfo(Long groupId, Long userId, Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = pageable.getPageNumber() * limit;
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("""
                select p.id as post_id, p.created, p.visibility, p.title, p.text, p.num_comments,
                u.id as post_user_id, u.username as post_user_username, l.value as like_value,
                	(select count(*) from groups_posts where group_id = ?) as num_posts,
                	(select count(*) from likes where post_id = p.id and value = 0) as num_dislike,
                	(select count(*) from likes where post_id = p.id and value = 1) as num_indifference,
                	(select count(*) from likes where post_id = p.id and value = 2) as num_like
                from groups_posts gp
                join posts p on p.id = gp.post_id
                join users u on u.id = p.user_id
                left join likes l on l.post_id = p.id and l.user_id = ?
                where gp.group_id = ?""");
        if (pageable.getSort().isSorted()) {
            sqlBuilder.append(" order by ");
            sqlBuilder.append(sortToSql(pageable.getSort()));
        }
        sqlBuilder.append(" limit ? offset ?");
        PostInfoRowMapper rowMapper = new PostInfoRowMapper(false, true);
        List<PostInfo> sliceContent = jdbcTemplate.query(sqlBuilder.toString(), rowMapper,
                groupId, userId, groupId, limit, offset);
        return new PageImpl<>(sliceContent, pageable, rowMapper.getNumPosts());
    }

    @Override
    public Optional<PostInfo> findPostInfoForGroupPost(Long postId, String loggedUsername) {
        String sql = """
                select p.id as post_id, p.created, p.visibility, p.title, p.text, p.num_comments,
                	u.id as post_user_id, u.username as post_user_username,
                    (select id from users where username = ?) as user_id, u2.username as username, l.value as like_value,
                	(select count(*) from likes where post_id = p.id and value = 0) as num_dislike,
                	(select count(*) from likes where post_id = p.id and value = 1) as num_indifference,
                	(select count(*) from likes where post_id = p.id and value = 2) as num_like,
                    g.id as group_id, g.name as group_name,
                    gu.is_admin as is_admin, (gu.request_accepted is not null) as is_member
                from posts p
                join users u on u.id = p.user_id
                left join likes l on l.post_id = p.id and l.user_id = (select id from users where username = ?)
                left join groups_posts gp on gp.post_id = p.id
                left join api_groups g on g.id = gp.group_id
                left join groups_users gu on gu.group_id = gp.group_id and gu.user_id = (select id from users where username = ?) 
                    and gu.request_accepted is not null
                left join users u2 on u2.id = gu.user_id
                where p.id = ?""";

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, new GroupPostInfoRowMapper(),
                    loggedUsername, loggedUsername, loggedUsername, postId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
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

    private String sortToSql(Sort sort) {
        StringBuilder sb = new StringBuilder();
        sort.get().forEach(o -> sb.append(", ").append(o.getProperty()).append(" ").append(o.getDirection()));
        return sb.substring(2);
    }
}