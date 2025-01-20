package com.ltev.connected.dao.impl.helper;

import com.ltev.connected.dto.ProfileInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileInfoRowMapper implements RowMapper<ProfileInfo> {

    private ProfileInfo profileInfo;

    public ProfileInfoRowMapper(ProfileInfo profileInfo) {
        this.profileInfo = profileInfo;
    }

    @Override
    public ProfileInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        profileInfo.setNumFriends(rs.getInt("num_friends"));
        profileInfo.setNumGroups(rs.getInt("num_groups"));
        return profileInfo;
    }
}