package com.ltev.connected.controller;

import com.ltev.connected.controller.support.ControllerSupport;
import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.RequestStatus;
import com.ltev.connected.domain.UserDetailsView;
import com.ltev.connected.dto.SearchInfo;
import com.ltev.connected.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/friends")
    public String showFriends(Model model) {
        model.addAttribute("friends", userService.findAllFriends());
        model.addAttribute("activeButton", "friends");
        return "user/friends";
    }

    @GetMapping("/friend-requests")
    public String showFriendshipRequests(Model model) {
        Map<RequestStatus, List<FriendRequest>> map = userService.findAllReceivedAndSentNotAcceptedFriendshipRequests();
        model.addAttribute("receivedFriendRequests", map.get(RequestStatus.RECEIVED));
        model.addAttribute("sentFriendRequests", map.get(RequestStatus.SENT));
        model.addAttribute("activeButton", "friendRequests");
        return "user/friend-requests";
    }

    @PostMapping(params = "action=send-friend-request")
    public String sendFriendRequest(Long profileId, HttpServletRequest request) {
        userService.sendFriendRequest(profileId);
        return ControllerSupport.redirectToLastUrl(request);
    }

    @PostMapping(params = "action=accept-friend-request")
    public String acceptFriendRequestPost(Long friendRequestId, HttpServletRequest request) {
        userService.acceptFriendRequest(friendRequestId);
        return ControllerSupport.redirectToLastUrl(request);
    }

    @GetMapping("/accept-{friendRequestId}")
    public String acceptFriendRequestGet(@PathVariable Long friendRequestId) {
        userService.acceptFriendRequest(friendRequestId);
        return "redirect:/friend-requests";
    }

    @GetMapping("/search")
    public String showSearchPage(Model model) {
        model.addAttribute("searchInfo", new SearchInfo());
        model.addAttribute("activeButton", "search");
        return "/user/search";
    }

    @PostMapping("/search")
    public String processSearch(SearchInfo searchInfo, Model model) {
        List<UserDetailsView> foundPeople = userService.searchForPeople(searchInfo);
        model.addAttribute("foundPeople", foundPeople);
        return "/user/search";
    }
}