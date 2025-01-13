package com.ltev.connected.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class GroupManagerInfo {

    private Long groupId;
    private String groupName;
    private int numRequestsToAccept;
}