package com.ltev.connected.controller;

import com.ltev.connected.domain.Group;
import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.service.GroupService;
import com.ltev.connected.service.support.GroupsRequestInfo;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static com.ltev.connected.utils.ApiUtils.isNumber;

@Controller
@RequestMapping({"group", "groups"})
@AllArgsConstructor
public class GroupController {

    private GroupService groupService;

    @GetMapping
    public String showGroupsPage(Model model) {
        GroupsRequestInfo groupsRequest = groupService.getGroupsRequest();
        model.addAttribute("groupsRequestInfo", groupsRequest);
        return "group/groups";
    }

    @GetMapping("new")
    public String showForm(Model model) {
        model.addAttribute("group", new Group());
        return "group/group-form";
    }

    @PostMapping("new")
    public String processForm(Group group) {
        try {
            groupService.createGroup(group);
        } catch (DataIntegrityViolationException e) {
            return "redirect:new?taken";
        }
        return "redirect:/groups?created";
    }

    @GetMapping("{id}")
    public String showGroup(@PathVariable("id") String strId, Model model) {
        Optional<GroupInfo> groupInfoOptional;

        if (isNumber(strId)
                && (groupInfoOptional = groupService.getGroupInfo(Long.valueOf(strId))).isPresent()) {
            model.addAttribute("groupInfo", groupInfoOptional.get());
            return groupInfoOptional.get().getGroupRequest().isMember()
                    ? "group/show-group-for-members"
                    : "group/show-group";
        }
        return "redirect:/";
    }

    @PostMapping(path = "{id}", params = "action=send-group-request")
    public String joinGroup(@PathVariable("id") Long groupId) {
        groupService.sendGroupRequest(groupId);
        return "redirect:" + groupId;
    }
}