package com.ltev.connected.controller;

import com.ltev.connected.domain.Post;
import com.ltev.connected.service.impl.PostServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping(params = "action=post-form")
    public String showPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "post-form";
    }

    @PostMapping(params = "action=new-post")
    public String createNewPost(Post post) {
        postService.save(post);
        return "redirect:/";
    }
}