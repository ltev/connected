package com.ltev.connected.controller;

import com.ltev.connected.domain.UserDetails;
import com.ltev.connected.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("account")
@AllArgsConstructor
public class AccountController {

    private AccountService accountService;

    @GetMapping("user-info")
    public String userInfoForm(Model model) {
        Optional<UserDetails> userDetailsOptional = accountService.findUserInfo();
        model.addAttribute("userDetails", userDetailsOptional.orElseGet(() -> new UserDetails()));
        model.addAttribute("activeButton", "account");
        return "account/user-info-form";
    }

    @PostMapping(value = "user-info", params = "action=save-details")
    public String processUserInfoForm(UserDetails userDetails) {
        accountService.saveUserInfo(userDetails);
        return "redirect:/account/user-info?success";
    }

    @PostMapping(value = "user-info", params = "action=delete-account")
    public String deleteAccount() {
        accountService.deleteAccount();
        return "redirect:/";
    }
}