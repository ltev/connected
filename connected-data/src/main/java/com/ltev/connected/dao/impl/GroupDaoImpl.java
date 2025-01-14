package com.ltev.connected.dao.impl;

import com.ltev.connected.dao.GroupDao;
import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.GroupRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.dto.GroupManagerInfo;
import com.ltev.connected.repository.GroupRepository;
import com.ltev.connected.repository.GroupRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
                        groupInfo.getGroup(),
                        new User(userId, rs.getString("user_username"))
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

    private static class GroupInfoExtractor implements ResultSetExtractor<GroupInfo> {

        private GroupRowMapper groupRowMapper = new GroupRowMapper();

        @Override
        public GroupInfo extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next() == false) {
                return null;
            }
            ArrayList<User> members = new ArrayList<>();
            ArrayList<User> admins = new ArrayList<>();

            Group group = groupRowMapper.mapRow(rs, -1);
            group.setMembers(members);
            group.setAdmins(admins);

            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setGroup(group);
            groupInfo.setNumMembers(rs.getInt("num_members"));

            Object userId = rs.getObject("user_id");
            if (userId == null) {
                return groupInfo;
            }

            do {
                User user = extractUser(rs);
                Boolean isAdmin = rs.getBoolean("is_admin");
                if (isAdmin) {
                    admins.add(user);
                } else {
                    members.add(user);
                }
            } while (rs.next());
            return groupInfo;
        }

        private User extractUser(ResultSet rs) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setUsername(rs.getString("user_username"));
            return user;
        }
    }

    private static final String FIND_GROUP_REQUEST_SQL = """
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
    public void deleteById(Long groupId) {
        groupRepository.deleteById(groupId);
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

    /**
     * @return GroupInfo with basic Group information (name, description), num_members and GroupRequest for logged user
     */
    @Override
    public Optional<GroupInfo> findGroupInfo(Long groupId, String loggedUsername) {
        String sql = """
                select g.id as id, g.created as created, g.name as name, g.description as description,
                    (select count(*) from groups_users where group_id = g.id and request_accepted is not null) as num_members,
                    gu.user_id as user_id, ? as user_username, gu.request_sent, gu.request_accepted, gu.is_admin
                from api_groups g
                left join groups_users gu on gu.group_id = g.id and gu.user_id = (select id from users where username = ?)
                where g.id = ?""";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, new GroupInfoRowMapper(),
                    loggedUsername, loggedUsername, groupId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * @return GroupInfo with basic Group information (name, description) + list of members and admins, num_members
     */
    @Override
    public Optional<GroupInfo> findGroupInfoWithMembers(Long groupId) {
        String sql = """
                select g.id as id, g.created as created, g.name as name, g.description as description,
                    (select count(*) from groups_users where group_id = g.id and request_accepted is not null) as num_members,
                    gu.user_id as user_id, u.username as user_username, gu.is_admin
                from api_groups g
                left join groups_users gu on gu.group_id = g.id and gu.request_accepted is not null
                left join users u on u.id = gu.user_id
                where g.id = ?""";
        return Optional.of(jdbcTemplate.query(sql, new GroupInfoExtractor(), groupId));
    }

    @Override
    public List<User> findAdmins(Long groupId) {
        String sql = FIND_GROUP_REQUEST_SQL
                + " and gu.is_admin = ?";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("user_username")),
                groupId, 1);
    }

    @Override
    public List<User> findMembers(Long groupId, int limit) {
        String sql = FIND_GROUP_REQUEST_SQL
                + " and gu.request_accepted is not null limit ?";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("user_username")),
                groupId, limit);
    }

    @Override
    public void saveGroupRequest(GroupRequest groupRequest) {
        groupRequestRepository.save(groupRequest);
    }

    @Override
    public List<GroupManagerInfo> findGroupsManagerInfoForAdmin(String adminName) {
        String sql = """
                select g.id, g.name,
                    (select count(*) from groups_users where group_id = g.id and request_accepted is null)
                    as num_requests_to_accept
                from api_groups g
                left join groups_users gu on g.id = gu.group_id
                where gu.is_admin = 1 and gu.user_id = (select id from users where username = ?)""";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new GroupManagerInfo(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("num_requests_to_accept")
                ), adminName);
    }

    @Override
    public int findCountByUsernameAndIsAdmin(String username) {
        String sql = """
                select count(*)
                from groups_users
                where user_id = (select id from users where username = ?) and is_admin = 1""";
        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }

    @Override
    public List<GroupRequest> findGroupRequestsByUsername(String username) {
        return groupRequestRepository.findByIdUserUsername(username);
    }

    @Override
    public void deleteGroupRequest(Long groupId, String username) {
        groupRequestRepository.deleteByIdGroupIdAndIdUserUsername(groupId, username);
    }
}