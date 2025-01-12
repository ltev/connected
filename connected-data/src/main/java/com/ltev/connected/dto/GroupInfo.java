package com.ltev.connected.dto;

import com.ltev.connected.domain.Group;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GroupInfo {

    private boolean isMember;
    private boolean isAdmin;
    private int numMembers;
    private Group group;
}