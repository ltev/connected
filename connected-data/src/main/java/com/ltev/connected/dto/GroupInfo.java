package com.ltev.connected.dto;

import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.GroupRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GroupInfo {

    private Group group;
    private int numMembers;
    private GroupRequest groupRequest;
}