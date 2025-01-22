package com.ltev.connected.dao;

import com.ltev.connected.domain.UserDetails;
import com.ltev.connected.dto.SearchInfo;

import java.util.List;

public interface UserDetailsDao {

    List<UserDetails> findBySearchInfo(SearchInfo searchInfo);
}