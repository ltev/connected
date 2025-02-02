package com.ltev.connected.controller;

import com.ltev.connected.domain.Message;
import com.ltev.connected.domain.Post;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/messages")
public class MessagesController {

    @GetMapping
    public String showMessagesMainPage() {
        return "messages/show-main";
    }

    @GetMapping("/{username}")
    public String showMessages(@PathVariable String username, Model model) {
        model.addAttribute("message", new Message());
        return "messages/message-form";
    }

    @PostMapping(path = "/{username}", params = "action=new-message")
    public String createNewPost(@PathVariable String username, @Valid Message message, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "messages/message-form";
        }

        return "redirect:/messages/" + username;
    }
}