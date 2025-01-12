package com.ltev.connected.controller;

import com.ltev.connected.domain.Group;
import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping({"group", "groups"})
@AllArgsConstructor
public class GroupController {

    private GroupService groupService;

    @GetMapping
    public String showGroupsPage(Model model) {
        List<Group> groups = groupService.getUserGroups();
        model.addAttribute("groups", groups);
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

        Long aLong = Long.valueOf(strId);
        if (isNumber(strId)
                && (groupInfoOptional = groupService.getGroupInfo(aLong)).isPresent()) {
            model.addAttribute("groupInfo", groupInfoOptional.get());
            return "group/show-group";
        }
        return "redirect:/";
    }

    // == PRIVATE HELPER METHODS ==

    private boolean isNumber(String str) {
        try {
            long l = Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}