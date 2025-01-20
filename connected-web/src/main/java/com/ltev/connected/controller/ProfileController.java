package com.ltev.connected.controller;

import com.ltev.connected.dto.ProfileInfo;
import com.ltev.connected.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
@AllArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/{username}")
    public String showUserProfile(@PathVariable("username") String profile, Authentication authentication, Model model) {

        // is logged user profile
        if (authentication != null && authentication.getName().equals(profile)) {
            return "redirect:/";
        }

        Optional<ProfileInfo> optionalProfileInfo = userService.getInformationForShowingProfile(profile);

        // check if profile exists
        if (optionalProfileInfo.isEmpty()) {
            return "redirect:/";
        }

        ProfileInfo profileInfo = optionalProfileInfo.get();
        model.addAttribute("profileInfo", profileInfo);

        if (profileInfo.isFriend()) {
            return "profile/show-profile-for-friends";
        } else {
            return "profile/show-profile-for-logged-users";
        }
    }
}