package com.ltev.connected.controller;

import com.ltev.connected.security.SecurityConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping(SecurityConfig.LOGIN_FORM_URL)
    public String showLoginPage(Model model) {
        model.addAttribute("actionUrl", SecurityConfig.AUTHENTICATE_USER_URL);
        return "security/login-form";
    }

    @GetMapping(SecurityConfig.ACCESS_DENIED_URL)
    public String showAccessDeniedPage(Model model) {
        model.addAttribute("actionUrl", SecurityConfig.AUTHENTICATE_USER_URL);
        return "security/access-denied";
    }
}
