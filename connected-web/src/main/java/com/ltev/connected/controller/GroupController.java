package com.ltev.connected.controller;

import com.ltev.connected.domain.Group;
import com.ltev.connected.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"group", "groups"})
@AllArgsConstructor
public class GroupController {

    private GroupService groupService;

    @GetMapping
    public String showGroupsPage() {
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
        } catch (DuplicateKeyException e) {
            return "redirect:/groups?taken";
        }
        return "redirect:/groups?taken";
    }
}
