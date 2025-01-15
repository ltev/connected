package com.ltev.connected.controller;

import com.ltev.connected.dto.PostInfo;
import com.ltev.connected.repository.PostRepository;
import com.ltev.connected.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class IndexController {

    private final PostService postService;
    PostRepository postRepository;

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        if (authentication != null) {
            List<PostInfo> ownPostsInfo = postService.findPostsInfo();
            model.addAttribute("ownPostsInfo", ownPostsInfo);

            List<PostInfo> friendsPostsInfo = postService.findFriendsPostsInfo();
            model.addAttribute("friendsPostsInfo", friendsPostsInfo);
            model.addAttribute("activeButton", "mainPage");
        }
        return "index";
    }
}