package com.ltev.connected.controller;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
@AllArgsConstructor
public class ProfileController {

    private final UserServiceImpl userService;

    @GetMapping("/{username}")
    public String showUserProfile(@PathVariable("username") String username, Authentication authentication, Model model) {

        // check if profile exists
        Optional<User> profile = userService.findByUsername(username);
        if (profile.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("profileId", profile.get().getId());

        if (authentication != null) {
            // check for friend request
            Optional<FriendRequest> friendRequest = userService.getFriendRequest(profile.get());
            FriendRequest.Status status = FriendRequest.Status.NOT_SENT;
            if (friendRequest.isPresent()) {
                status = friendRequest.get().getStatus(authentication.getName());
                model.addAttribute("friendRequestId", friendRequest.get().getId());
            }
            model.addAttribute("friendRequestStatus", status);
        }

        return "profile/profile";
    }

    @PostMapping(value = "/{username}", params = "action=send-friend-request")
    public String sendFriendRequest(@PathVariable String username, Long profileId) {
        userService.sendFriendRequest(profileId);
        return "redirect:/profile/" + username;
    }

    @PostMapping(value = "/{username}", params = "action=accept-friend-request")
    public String acceptFriendRequest(@PathVariable String username, Long friendRequestId) {
        userService.acceptFriendRequest(friendRequestId);
        return "redirect:/profile/" + username;
    }
}
