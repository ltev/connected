package com.ltev.connected.controller;

import com.ltev.connected.domain.GroupRequest;
import com.ltev.connected.dto.GroupManagerInfo;
import com.ltev.connected.service.GroupManagerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.ltev.connected.utils.ApiUtils.isNumber;

@Controller
@RequestMapping("group-manager")
@AllArgsConstructor
public class GroupManagerController {

    private final GroupManagerService groupManagerService;

    @GetMapping
    public String showGroupsManagerPage(Model model) {
        List<GroupManagerInfo> groupsManagerInfo = groupManagerService.findGroupsManagerInfoForAdmin();
        model.addAttribute("groupsManagerInfo", groupsManagerInfo);
        return "group-manager/show-groups-info";
    }

    @GetMapping("{groupId}")
    public String showGroupManagerPage(@PathVariable("groupId") String strGroupId, Model model) {
        if (! isNumber(strGroupId)) {
            return "redirect:/";
        }
        List<GroupRequest> groupRequests = groupManagerService.getSentGroupRequests(Long.valueOf(strGroupId));
        model.addAttribute("groupName", groupRequests.size() > 0
                ? groupRequests.get(0).getId().getGroup().getName()
                : "group id: " + strGroupId);
        model.addAttribute("groupRequests", groupRequests);
        return "group-manager/show-group-info";
    }

    @GetMapping("accept/{groupId}-{userId}")
    public String acceptGroupRequest(@PathVariable("groupId") String strGroupId,
                                     @PathVariable("userId") String strUserId) {
        if (isNumber(strGroupId) && isNumber(strUserId)) {
            groupManagerService.acceptGroupRequest(Long.valueOf(strGroupId), Long.valueOf(strUserId));
            return "redirect:/group-manager/" + strGroupId;
        }
        return "redirect:/";
    }
}