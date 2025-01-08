package com.ltev.connected.controller;

import com.ltev.connected.domain.User;
import com.ltev.connected.service.PostService;
import com.ltev.connected.service.impl.UserServiceImpl;
import com.ltev.connected.dto.PostInfo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class IndexController {

    private final UserServiceImpl userService;
    private final PostService postService;

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {

        if (authentication != null) {
            User user = userService.findByUsername(authentication.getName()).get();
            model.addAttribute("ownPosts", user.getPosts());

            // boolean isInitialized = Hibernate.isInitialized(user.getPosts());

            List<PostInfo> friendsPostsInfo = postService.findFriendsPostsInfo();
            model.addAttribute("friendsPostsInfo", friendsPostsInfo);
            model.addAttribute("activeButton", "mainPage");
        }
        return "index";
    }
}