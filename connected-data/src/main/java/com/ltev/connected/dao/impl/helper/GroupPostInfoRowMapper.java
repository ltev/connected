package com.ltev.connected.dao.impl.helper;

import com.ltev.connected.domain.Group;
import com.ltev.connected.dto.PostInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupPostInfoRowMapper implements RowMapper<PostInfo> {

    private PostInfoRowMapper postInfoRowMapper = new PostInfoRowMapper(true);

    /**
     *          PostInfoRowMapper:
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
     *
     *          Here:
     *     group_id
     *     group_name
     *     is_member
     *     is_admin
     */
    @Override
    public PostInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        PostInfo postInfo = postInfoRowMapper.mapRow(rs, -1);

        postInfo.setGroupAdmin(rs.getInt("is_admin") == 1);
        postInfo.setGroupMember(rs.getInt("is_member") == 1);

        Group group = new Group(rs.getLong("group_id"), rs.getString("group_name"));
        postInfo.getPost().setGroup(group);

        return postInfo;
    }
}