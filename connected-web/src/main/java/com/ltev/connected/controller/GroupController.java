package com.ltev.connected.controller;

import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.Post;
import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.dto.PostInfo;
import com.ltev.connected.exception.AccessDeniedException;
import com.ltev.connected.exception.PageOutOfBoundsException;
import com.ltev.connected.service.GroupService;
import com.ltev.connected.service.PostService;
import com.ltev.connected.service.support.GroupsRequestInfo;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.ltev.connected.utils.ApiUtils.isNumber;

@Controller
@RequestMapping({"group", "groups"})
@AllArgsConstructor
public class GroupController {

    private GroupService groupService;
    private PostService postService;

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
    public String showGroup(@PathVariable("id") String strId,
                            @RequestParam(name = "page", required = false, defaultValue = "1") String strPageNumber,
                            Model model) {
        Optional<GroupInfo> groupInfoOptional;

        if (isNumber(strId)) {
            try {
                Pageable pageable = PageRequest.of(Integer.parseInt(strPageNumber) - 1, 3,
                        Sort.by(Sort.Order.desc("created")));

                if ((groupInfoOptional = groupService.getGroupInfo(Long.valueOf(strId), pageable)).isPresent()) {
                    model.addAttribute("groupInfo", groupInfoOptional.get());
                    return groupInfoOptional.get().getGroupRequest().isMember()
                            ? "group/show-group-for-members"
                            : "group/show-group";
                }
            } catch (IllegalArgumentException | PageOutOfBoundsException e) {
                return "redirect:/group/" + strId;
            }
        }
        return"redirect:/";
    }

    @PostMapping(path = "{id}", params = "action=post-form")
    public String showPostForm(@PathVariable("id") Long groupId, @RequestParam String groupName, Model model) {
        Post post = new Post();
        post.setGroup(new Group(groupId, groupName));

        model.addAttribute("post", post);
        return "group/group-post-form";
    }

    @PostMapping(path = "{groupId}", params = "action=new-post")
    public String createNewPost(@PathVariable("groupId") Long groupId, Post post) {
        groupService.saveGroupPost(post);
        return "redirect:/group/" + groupId;
    }

    @GetMapping("{id}/post/{postId}")
    public String showGroupPost(@PathVariable Long id, @PathVariable Long postId, Model model) {
        Optional<PostInfo> postInfoOptional = postService.getPostInfoForGroupPost(postId);

        if (postInfoOptional.isPresent()) {
            model.addAttribute("postInfo", postInfoOptional.get());
            if (postInfoOptional.get().getLoggedUser() != null) {
                model.addAttribute("loggedUserId", postInfoOptional.get().getLoggedUser().getId());
            }
            return "post/show-post";
        }
        return "redirect:/";
    }

    @PostMapping(path = "{id}", params = "action=send-group-request")
    public String joinGroup(@PathVariable("id") Long groupId) {
        groupService.sendGroupRequest(groupId);
        return "redirect:" + groupId;
    }

    @PostMapping(path = "{id}", params = "action=leave-group")
    public String leaveGroup(@PathVariable("id") Long groupId) {
        groupService.leaveGroup(groupId);
        return "redirect:" + groupId;
    }

    @GetMapping("{id}/members")
    public String showMembers(@PathVariable("id") String strGroupId, Model model) {
        try {
            Optional<GroupInfo> groupOptional = groupService.getGroupInfoWithMembers(Long.valueOf(strGroupId));
            if (groupOptional.isPresent()) {
                model.addAttribute("groupInfo", groupOptional.get());
                return "group/show-members";
            }
        } catch (NumberFormatException | AccessDeniedException e) {
        }
        return "redirect:/";
    }
}