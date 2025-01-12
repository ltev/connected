package com.ltev.connected.service;

import com.ltev.connected.domain.Group;

import java.util.Optional;

public interface GroupService {

    void createGroup(Group group);

    Optional<Group> getGroup(Long id);
}