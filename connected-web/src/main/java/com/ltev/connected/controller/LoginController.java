package com.ltev.connected.controller;

import com.ltev.connected.security.SecurityConfig;
import com.ltev.connected.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping(SecurityConfig.LOGIN_FORM_URL)
    public String showLoginPage(Model model) {
        model.addAttribute("actionUrl", SecurityConfig.AUTHENTICATE_USER_URL);
        return "login/login-form";
    }

    @GetMapping(SecurityConfig.ACCESS_DENIED_URL)
    public String showAccessDeniedPage(Model model) {
        model.addAttribute("actionUrl", SecurityConfig.AUTHENTICATE_USER_URL);
        return "security/access-denied";
    }

    @GetMapping("signup")
    public String showSignupPage() {
        return "login/signup-form";
    }

    @PostMapping("signup")
    public String createNewUser(String username, String password, BCryptPasswordEncoder encoder) {
        String encodedPassword = "{bcrypt}" + encoder.encode(password);
        try {
            userService.createNewUser(username, encodedPassword);
        } catch (DuplicateKeyException e) {
            return "redirect:/signup?taken";
        }
        return "redirect:/login?created";
    }
}