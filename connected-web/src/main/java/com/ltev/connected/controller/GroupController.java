package com.ltev.connected.controller;

import com.ltev.connected.domain.Group;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"group", "groups"})
public class GroupController {

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
        System.out.println(group);
        return "redirect:/groups?created";
    }
}
