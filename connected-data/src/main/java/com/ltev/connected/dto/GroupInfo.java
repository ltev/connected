package com.ltev.connected.dto;

import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.GroupRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@Setter
@ToString
public class GroupInfo {

    private Group group;
    private int numMembers;
    private GroupRequest groupRequest;
    private Page<PostInfo> postInfoPage;

    public boolean hasPosts() {
        return getNumPosts() > 0;
    }

    public long getNumPosts() {
        return postInfoPage.getTotalElements();
    }
}