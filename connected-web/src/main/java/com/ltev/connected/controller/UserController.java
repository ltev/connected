package com.ltev.connected.controller;

import com.ltev.connected.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/friends")
    public String showFriends(Model model) {
        model.addAttribute("friends", userService.findAllFriends());
        return "user/friends";
    }

    @GetMapping("/friend-requests")
    public String showFriendshipRequests(Model model) {
        model.addAttribute("friendRequests", userService.findAllReceivedFriendshipRequests());
        return "user/friend-requests";
    }
}