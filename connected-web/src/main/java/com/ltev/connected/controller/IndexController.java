package com.ltev.connected.controller;

import com.ltev.connected.domain.User;
import com.ltev.connected.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class IndexController {

    private final UserServiceImpl userService;

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        if (authentication != null) {
            User user = userService.findByUsername(authentication.getName()).get();
            model.addAttribute("ownPosts", user.getPosts());
        }
        return "index";
    }
}
