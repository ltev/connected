package com.ltev.connected.dao.impl.helper;

import com.ltev.connected.domain.Like;
import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.PostInfo;
import lombok.Getter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

public class PostInfoRowMapper implements RowMapper<PostInfo> {

    private boolean withLoggedUser;
    private boolean storeNumPosts;
    @Getter
    private int numPosts = 0;

    public PostInfoRowMapper(boolean withLoggedUser) {
        this.withLoggedUser = withLoggedUser;
    }

    public PostInfoRowMapper(boolean withLoggedUser, boolean storeNumPosts) {
        this.withLoggedUser = withLoggedUser;
        this.storeNumPosts = storeNumPosts;
    }

    /**
     *     optional -> num_posts
     *
     *     p.id as post_id,
     *     p.created,
     *     p.visibility,
     *     p.title,
     *     p.text,
     *     p.num_comments,
     *     u.id as post_user_id,
     *     u.username as post_user_username,
     *     user_id -> logged user id
     *     username -> logged username
     *     l.value as like_value,
     *     num_dislike,
     *     num_indifference,
     *     num_like
     */
    @Override
    public PostInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (storeNumPosts && numPosts == 0) {
            numPosts = (int) rs.getLong("num_posts");
        }

        User postUser = new User();
        postUser.setId(rs.getLong("post_user_id"));
        postUser.setUsername(rs.getString("post_user_username"));

        Post post = new Post();
        post.setUser(postUser);
        post.setCreated(rs.getTimestamp("created").toInstant().atZone(ZoneId.systemDefault()));
        post.setVisibility(Post.Visibility.ofOrdinal(rs.getByte("visibility")));
        post.setId(rs.getLong("post_id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));
        post.setNumComments(rs.getLong("num_comments"));

        PostInfo postInfo = new PostInfo();
        postInfo.setPost(post);
        postInfo.setNumLikes(rs.getInt("num_like"));
        postInfo.setNumIndifferences(rs.getInt("num_indifference"));
        postInfo.setNumDislikes(rs.getInt("num_dislike"));

        Integer likeOrdinal = (Integer) rs.getObject("like_value");   // do not use getByte -> default value 0 not null
        if (likeOrdinal != null) {
            postInfo.setLikeValue(Like.Value.ofOrdinal(likeOrdinal));
        }

        if (withLoggedUser) {
            User loggedUser = new User();
            loggedUser.setId(rs.getLong("user_id"));
            loggedUser.setUsername(rs.getString("username"));
            postInfo.setLoggedUser(loggedUser);
        }
        return postInfo;
    }
}