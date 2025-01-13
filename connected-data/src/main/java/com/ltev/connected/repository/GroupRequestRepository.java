package com.ltev.connected.repository;

import com.ltev.connected.domain.GroupRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRequestRepository extends JpaRepository<GroupRequest, GroupRequest.Id> {
}