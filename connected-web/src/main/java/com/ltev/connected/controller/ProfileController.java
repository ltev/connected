package com.ltev.connected.controller;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.service.UserService;
import com.ltev.connected.service.support.ProfileInfo;
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

    private final UserService userService;

    @GetMapping("/{username}")
    public String showUserProfile(@PathVariable("username") String username, Authentication authentication, Model model) {

        // is logged user profile
        if (authentication != null && authentication.getName().equals(username)) {
            return "redirect:/";
        }

        Optional<ProfileInfo> optionalProfileInfo = userService.getInformationForShowingProfile(username);

        // check if profile exists
        if (optionalProfileInfo.isEmpty()) {
            return "redirect:/";
        }

        ProfileInfo profileInfo = optionalProfileInfo.get();

        // is logged
        if (profileInfo.getLoggedUsername().isPresent()) {
            profileInfo.getFriendRequest().ifPresent(request ->
                    model.addAttribute("friendRequestId", request.getId()));
            FriendRequest.Status status = profileInfo.getFriendRequest().isPresent()
                    ? profileInfo.getFriendRequest().get().getStatus(profileInfo.getLoggedUsername().get())
                    : FriendRequest.Status.NOT_SENT;
            model.addAttribute("friendRequestStatus", status);
        }
        model.addAttribute("profileId", profileInfo.getProfileUser().getId());

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