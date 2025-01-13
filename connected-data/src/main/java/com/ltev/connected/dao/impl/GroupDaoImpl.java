package com.ltev.connected.dao.impl;

import com.ltev.connected.dao.GroupDao;
import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.GroupRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.repository.GroupRepository;
import com.ltev.connected.repository.GroupRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class GroupDaoImpl implements GroupDao {

    private static class GroupRowMapper implements RowMapper<Group> {

        @Override
        public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
            Group group = new Group();
            group.setId(rs.getLong("id"));
            group.setCreated(rs.getTimestamp("created").toInstant().atZone(ZoneId.systemDefault()));
            group.setName(rs.getString("name"));
            group.setDescription(rs.getString("description"));
            return group;
        }
    }

    private static class GroupInfoRowMapper implements RowMapper<GroupInfo> {

        private GroupRowMapper groupRowMapper = new GroupRowMapper();

        @Override
        public GroupInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setGroup(groupRowMapper.mapRow(rs, -1));
            groupInfo.setNumMembers(rs.getInt("num_members"));

            GroupRequest groupRequest = new GroupRequest();
            long userId = rs.getLong("user_id");

            // if request exists
            if (userId > 0) {
                groupRequest.setId(new GroupRequest.Id(
                        groupInfo.getGroup().getId(),
                        userId
                ));
                groupRequest.setSent(rs.getDate("request_sent").toLocalDate());
                Date requestAccepted = rs.getDate("request_accepted");
                if (requestAccepted != null) {
                    groupRequest.setAccepted(requestAccepted.toLocalDate());
                }
                groupRequest.setIsAdmin(rs.getByte("is_admin"));
            }
            groupInfo.setGroupRequest(groupRequest);
            return groupInfo;
        }
    }

    private static final String FIND_GROUP_MEMBERS_SQL = """
            select u.id as user_id, u.username as user_username
            from api_groups g
            left join groups_users gu on g.id = gu.group_id
            left join users u on u.id = gu.user_id
            where g.id = ?""";

    private final JdbcTemplate jdbcTemplate;
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;

    @Override
    public void save(Group group) {
        groupRepository.save(group);
    }

    @Override
    public void saveGroupAdmin(Long id, String username) {
        String sql = """
        insert into groups_users (group_id, user_id, request_sent, request_accepted, is_admin)
        values (?, (select id from users where username = ?), ?, ?, ?)""";
        jdbcTemplate.update(sql,
                id, username, LocalDate.now(), LocalDate.now(), 1);
    }

    @Override
    public List<Group> findGroupsByUsername(String username) {
        String sql = """
            select g.id, g.created, g.name, g.description
            from api_groups g
            left join groups_users gu on g.id = gu.group_id
            left join users u on u.id = gu.user_id
            where u.username = ?""";
        return jdbcTemplate.query(sql, new GroupRowMapper(), username);
    }

    @Override
    public Optional<GroupInfo> findGroupInfo(Long groupId, String loggedUsername) {
        String sql = """
                select g.id as id, g.created as created, g.name as name, g.description as description,
                    (select count(*) from groups_users where group_id = g.id and request_accepted is not null) as num_members,
                    gu.user_id as user_id, gu.request_sent, gu.request_accepted, gu.is_admin
                from api_groups g
                left join groups_users gu on gu.group_id = g.id and gu.user_id = (select id from users where username = ?)
                where g.id = ?""";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, new GroupInfoRowMapper(), loggedUsername, groupId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAdmins(Long groupId) {
        String sql = FIND_GROUP_MEMBERS_SQL
                + " and gu.is_admin = ?";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("user_username")),
                groupId, 1);
    }

    @Override
    public List<User> findMembers(Long groupId, int limit) {
        String sql = FIND_GROUP_MEMBERS_SQL
                + "  limit ?";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("user_username")),
                groupId, limit);
    }

    @Override
    public void saveGroupRequest(GroupRequest groupRequest) {
        groupRequestRepository.save(groupRequest);
    }
}