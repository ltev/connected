package com.ltev.connected.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/friends")
    public String showFriends(Model model, Authentication authentication) {
        return "user/friends";
    }
}