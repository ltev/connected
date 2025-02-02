package com.ltev.connected.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("messages")
public class MessagesController {

    @GetMapping
    public String showMessagesMainPage() {
        return "messages/show-main";
    }
}