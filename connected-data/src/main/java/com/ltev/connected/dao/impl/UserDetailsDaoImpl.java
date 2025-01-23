package com.ltev.connected.dao.impl;

import com.ltev.connected.dao.UserDetailsDao;
import com.ltev.connected.domain.User;
import com.ltev.connected.domain.UserDetails;
import com.ltev.connected.dto.SearchInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDetailsDaoImpl implements UserDetailsDao {

    private static class UserDetailsRowMapper implements RowMapper<UserDetails> {

        @Override
        public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("id");
            UserDetails userDetails = new UserDetails();
            userDetails.setId(id);
            userDetails.setFirstName(rs.getString("first_name"));
            userDetails.setLastName(rs.getString("last_name"));
            Date birthday = rs.getDate("birthday");
            if (birthday != null) {
                userDetails.setBirthday(birthday.toLocalDate());
            }
            userDetails.setUser(new User(id, rs.getString("username")));
            return userDetails;
        }
    }

    private JdbcTemplate jdbcTemplate;

    public UserDetailsDaoImpl(@Qualifier("userDataJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<UserDetails> findBySearchInfo(SearchInfo searchInfo) {
        StringBuilder sqlBuilder = new StringBuilder("select * from user_data_view where ");
        String[] properties = searchInfo.getSearchByProperties();
        if (properties.length == 0) {
            return Collections.emptyList();
        }
        boolean first = true;
        for (String property : properties) {
            if (!first) {
                sqlBuilder.append("and ");
                first = false;
            }
            if (property.equals("age")) {
                sqlBuilder.append(" birthday between ? and ?");
            } else {
                sqlBuilder.append(property);
                sqlBuilder.append(" = ? ");
            }
        }
        String sql = sqlBuilder.toString();
        System.out.println(sql);
        return jdbcTemplate.query(sql, new UserDetailsRowMapper(), searchInfo.getSearchByValues());
    }
}