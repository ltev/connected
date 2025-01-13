package com.ltev.connected.repository;

import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.GroupRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GroupRequestRepository extends JpaRepository<GroupRequest, GroupRequest.Id> {

    List<GroupRequest> findByIdGroupAndAccepted(Group group, LocalDate accepted);

    Optional<GroupRequest> findByIdGroupIdAndIdUserUsername(Long groupId, String username);

    @Transactional
    @Modifying
    @Query("update GroupRequest r set r.accepted = current_date"
            + " where r.accepted is null and r.id.group.id = ?1 and r.id.user.id = ?2")
    void acceptGroupRequest(Long groupId, Long userId);
}